package xyz.defe.sp.order.service;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import jakarta.transaction.Transactional;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.AsyncRabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import xyz.defe.sp.common.Cache;
import xyz.defe.sp.common.Const;
import xyz.defe.sp.common.entity.spOrder.SpOrder;
import xyz.defe.sp.common.entity.spProduct.Product;
import xyz.defe.sp.common.exception.ExceptionUtil;
import xyz.defe.sp.common.pojo.Cart;
import xyz.defe.sp.common.pojo.DeductionResult;
import xyz.defe.sp.common.pojo.OrderMsg;
import xyz.defe.sp.common.pojo.PageQuery;
import xyz.defe.sp.common.util.CheckParam;
import xyz.defe.sp.order.dao.OrderDao;

import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
public class OrderServiceImpl implements OrderService {
    @Autowired
    private Gson gson;
    @Autowired
    private Cache cache;
    @Autowired
    private OrderDao orderDao;
    @Autowired
    private ProductService productService;
    @Autowired
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

        RLock lock = redisson.getLock(Const.LOCK_KEY_ORDER_TOKEN_PREFIX + cart.getOrderToken());
        if (!lock.tryLock()) {return;}
        try {
            Object obj = cache.get(Const.TOKEN_ORDER_PREFIX + cart.getOrderToken());
            if (obj == null) {
                cache.put(Const.TOKEN_ORDER_PREFIX + cart.getOrderToken(), 1, 5, TimeUnit.MINUTES);
            } else {
                ExceptionUtil.warn("duplicate submission");
            }
        } finally {
            lock.unlock();
        }
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
        //save message to local table
        localMessageService.saveOrderMessage(message, 1);
        log.info("save OrderMsg,id={}", message.getId());

        //send message
        sendOrderMsg(message, false);

        return order;
    }

    @Override
    public SpOrder getOrder(String id) {
        Optional<SpOrder> order = orderDao.findById(id);
        if (order.isPresent()) {
            return order.get();
        }
        return null;
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

    @Override
    public SpOrder getToPayOrder(String id) {
        return orderDao.getToPayOrder(id);
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
        //RabbitMQ RPC - Request/Reply Pattern
        //send product quantity deduction request to PRODUCT SERVICE
        asyncRabbitTemplate.convertSendAndReceive(Const.EXCHANGE_ORDER, Const.ROUTING_KEY_DEDUCT_QUANTITY_REQUEST, msg)
                .whenComplete((result, ex) -> {
                    DeductionResult deductionResult = (DeductionResult) result;
                    log.debug("got product quantity deduction result from PRODUCT SERVICE,result={},order id={}",
                            deductionResult.isSuccessful(), msg.getOrderId());

                    if (ex == null) {   //on success
                        if (deductionResult.isSuccessful()) {
                            //set order paymentState=1(to pay)
                            setOrderPaymentState(msg.getOrderId(), 1);
                            log.info("deduct quantity successful,set paymentState=1(to pay),order id={}", msg.getOrderId());
                        } else {    //if deduct quantity failed then set order invalid
                            orderDao.setOrderInvalid(msg.getOrderId(), deductionResult.getMessage());
                            log.info("deduct quantity failed,set order invalid,order id={}", msg.getOrderId());
                        }
                        if (isReSend) {
                            localMessageService.setRetry(msg.getId(), 0);
                        }
                    } else {    //on failure
                        if (isReSend) {
                            localMessageService.setRetry(msg.getId(), 0);
                        } else {
                            localMessageService.setRetry(msg.getId(), 1);
                        }
                        log.error("send message to MQ failed,OrderMsg id={},{}", msg.getId(), ex.getMessage());
                        ex.printStackTrace();
                    }

                });

        log.debug("send product quantity deduction request to PRODUCT SERVICE");
    }

}
