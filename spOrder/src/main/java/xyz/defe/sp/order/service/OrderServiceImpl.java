package xyz.defe.sp.order.service;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import jakarta.transaction.Transactional;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.AsyncRabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import xyz.defe.sp.common.Const;
import xyz.defe.sp.common.entity.spOrder.SpOrder;
import xyz.defe.sp.common.entity.spProduct.Product;
import xyz.defe.sp.common.enums.LocalMsgState;
import xyz.defe.sp.common.exception.ExceptionUtil;
import xyz.defe.sp.common.pojo.Cart;
import xyz.defe.sp.common.pojo.DeductionResult;
import xyz.defe.sp.common.pojo.OrderMsg;
import xyz.defe.sp.common.pojo.PageQuery;
import xyz.defe.sp.common.util.CheckParam;
import xyz.defe.sp.order.dao.OrderDao;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.CompletableFuture;

@Service
public class OrderServiceImpl implements OrderService {
    @Autowired
    private Gson gson;
    @Autowired
    private OrderDao orderDao;
    @Autowired
    private ProductService productService;
    @Autowired
    @Qualifier("orderTemplate")
    private AsyncRabbitTemplate asyncRabbitTemplate;
    @Autowired
    private LocalMessageService localMessageService;
    @Autowired
    public RedissonClient redisson;
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    public String getOrderToken() {
        return UUID.randomUUID().toString();
    }

    private void checkCart(Cart cart) {
        CheckParam.check(cart.getUid(), "uid is null or empty");
        CheckParam.check(cart.getCounterMap(), "cart is null or empty");
        CheckParam.check(cart.getOrderToken(), "orderToken is null or empty");

        boolean notExist = redisson.getBucket(Const.TOKEN_ORDER_PREFIX + cart.getOrderToken())
                .setIfAbsent(1, Duration.ofMinutes(30));
        if (!notExist) {ExceptionUtil.warn("duplicate submission");}
    }

    @Override
    @Transactional
    public SpOrder newOrder(Cart cart) {
        checkCart(cart);
        Set<String> productIdSet = cart.getCounterMap().keySet();
        List<Product> products = productService.getProducts(productIdSet);
        cart.setProductSet(new HashSet<>(products));

        //create order
        SpOrder order = new SpOrder(cart.getUid(), gson.toJson(cart));
        order = orderDao.saveAndFlush(order);
        log.info("created a order,id={}", order.getId());

        //create a message(deduct product quantity) to PRODUCT SERVICE
        OrderMsg message = new OrderMsg();
        message.setOrderId(order.getId());
        message.setCounterMap(cart.getCounterMap());
        message.setFrom(Const.ORDER_SERVER);
        message.setTo(Const.PRODUCT_SERVER);
        message.setRemark("deduct product quantity rquest");
        //save message to local table
        localMessageService.saveOrderMessage(message);
        log.info("save OrderMsg,id={}", message.getId());

        //send message
        sendOrderMsg(message, false);

        return order;
    }

    @Override
    public SpOrder getOrder(String id) {
        Optional<SpOrder> order = orderDao.findById(id);
        return order.orElse(null);
    }

    @Override
    public List<SpOrder> getValidOrders(String uid, PageQuery pageQuery) {
        return orderDao.getValidOrders(uid, pageQuery.getPageRequest());
    }

    @Override
    public List<SpOrder> getUnpaidOrders(String uid) {
        return orderDao.getUnpaidOrders(uid);
    }

    @Override
    public List<SpOrder> getUnpaidOrders() {
        return orderDao.getUnpaidOrders();
    }

    @Override
    @Transactional
    public void setOrderState(String id, boolean state) {
        orderDao.setOrderState(id, state);
    }

    @Override
    @Transactional
    public void setOrderPaymentState(String id, int state) {
        orderDao.setOrderPaymentState(id, state);
    }

    //wait for product quantity deduction completed and order payment state changed to 1(to pay)
    @Override
    public SpOrder getToPayOrder(String id) {
        SpOrder order = getAndCheckToPayOrder(id);
        int n = 0;
        while (order == null && n < 30) {
            try {
                Thread.sleep(100);
                log.info("getToPayOrder .... n={}", n);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            order = getAndCheckToPayOrder(id);;
            n++;
        }
        return order;
    }

    private SpOrder getAndCheckToPayOrder(String orderId) {
        SpOrder order = null;
        try {
            //to get updated order
            order = CompletableFuture.supplyAsync(() -> getOrder(orderId)).get();
        } catch (Exception e) {
            log.error("get to pay order failed,order id={},error={}", orderId, e.getMessage(), e);
            ExceptionUtil.warn("get to pay order failed,order id=" + orderId + ",error=" + e.getMessage());
        }
        if (order == null) {
            ExceptionUtil.warn("The order does not exists,order id = " + orderId);
        }
        if (!order.isValid()) {
            ExceptionUtil.warn("the order is not able to pay,order id="
                    + orderId + ", invalid reason:" + order.getInvalidReason());
        } else if (order.getPaymentState() == 1) {
            return order;
        }
        return null;
    }

    @Override
    public SpOrder getPaidOrder(String id) {
        return orderDao.getPaidOrder(id);
    }

    public void saveAll(Iterable<SpOrder> entities) {
        orderDao.saveAll(entities);
    }

    /**
     * when unpaid orders are expired(such as expired time=30 minutes)
     * then request to PRODUCT SERVICE to restore product's quantity
     * if successful then set orders invalid
     */
    public void processExpiredOrders() {
        Map<String, Map<String, Integer>> restoreMap = new HashMap<>();
        List<SpOrder> orders = getUnpaidOrders();
        for (SpOrder order : orders) {
            if (System.currentTimeMillis() - order.getCreatedTime().getTime() > Const.EXPIRED_TIME_ORDER_MILLIS) {
                Map<String, Integer> counterMap = ((Cart)gson.fromJson(order.getCartJson(),
                        new TypeToken<Cart>(){}.getType())).getCounterMap();
                restoreMap.put(order.getId(), counterMap);
            }
        }
        Set<String> successfulOrderIdSet = productService.reStoreQuantity(restoreMap);
        if (successfulOrderIdSet != null && !successfulOrderIdSet.isEmpty()) {
            orders.removeIf(order -> !successfulOrderIdSet.contains(order.getId()));
            orders.forEach(order -> order.setValid(false));
            saveAll(orders);
        }
    }

    //OrderMsg has the same id as LocalMessage
    public void sendOrderMsg(OrderMsg msg, boolean isReSend) {
        log.debug("send product quantity deduction request to PRODUCT SERVICE, in sendOrderMsg()");

        //send product quantity deduction request to PRODUCT SERVICE
        asyncRabbitTemplate.convertSendAndReceive(msg).whenComplete((result, ex) -> {
            if (ex == null) {   //on success
                DeductionResult deductionResult = ((OrderMsg) result).getDeductionResult();

                log.debug("got product quantity deduction result from PRODUCT SERVICE,result={},order id={}",
                        deductionResult.isSuccessful(), msg.getOrderId());

                if (deductionResult.isSuccessful()) {
                    //set order paymentState=1(to pay)
                    setOrderPaymentState(msg.getOrderId(), 1);

                    log.info("deduct quantity successful,set paymentState=1(to pay),order id={}", msg.getOrderId());
                } else {    //if deduct quantity failed then set order invalid
                    orderDao.setOrderInvalid(msg.getOrderId(),
                            "deduction result from PRODUCT SERVICE: " + deductionResult.getMessage());

                    log.info("deduct quantity failed,set order invalid,order id={}", msg.getOrderId());
                }
                localMessageService.setMessageState(msg.getId(), LocalMsgState.SENT);
            } else {    //on failure
                if (isReSend) {
                    localMessageService.setMessageState(msg.getId(), LocalMsgState.EXCEPTION);
                } else {
                    localMessageService.setMessageState(msg.getId(), LocalMsgState.PENDING_RESEND);
                }
                log.error("send message to MQ failed,OrderMsg id={},{}", msg.getId(), ex.getMessage(), ex);
            }

        });
    }

}
