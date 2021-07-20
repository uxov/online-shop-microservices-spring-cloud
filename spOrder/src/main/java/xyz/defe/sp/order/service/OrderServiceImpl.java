package xyz.defe.sp.order.service;

import com.google.common.base.Strings;
import com.google.gson.Gson;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import xyz.defe.sp.common.Cache;
import xyz.defe.sp.common.Const;
import xyz.defe.sp.common.ExceptionUtil;
import xyz.defe.sp.common.WarnException;
import xyz.defe.sp.common.entity.spOrder.SpOrder;
import xyz.defe.sp.common.entity.spProduct.Product;
import xyz.defe.sp.common.pojo.Cart;
import xyz.defe.sp.common.pojo.OrderMsg;
import xyz.defe.sp.common.pojo.PageQuery;
import xyz.defe.sp.order.dao.OrderDao;

import javax.transaction.Transactional;
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
    private LocalMessageService localMessageService;
    @Autowired
    private MqMessageService mqMessageService;
    @Autowired
    public RedissonClient redisson;
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    public String getOrderToken() {
        String token = ((int)(Math.random() * 100000000)) + "";
        return token;
    }

    void checkCart(Cart cart) throws WarnException {
        if (Strings.isNullOrEmpty(cart.getUid())) {
            ExceptionUtil.warn("uid is empty");
        }
        if (cart.getCounterMap() == null || cart.getCounterMap().isEmpty()) {
            ExceptionUtil.warn("cart is empty");
        }
        if (Strings.isNullOrEmpty(cart.getOrderToken())) {
            ExceptionUtil.warn("orderToken is empty");
        } else {
            RLock lock = redisson.getLock(Const.LOCK_KEY_ORDER_TOKEN_PREFIX + cart.getOrderToken());
            try {
                lock.lock();
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
    }

    @Override
    @Transactional
    public SpOrder newOrder(Cart cart) throws Exception {
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
        mqMessageService.sendOrderMsg(message, 1);

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

    /**
     * maybe the order is processing then retry
     * order's paymentState:{0:init state, 1:to pay, 2:paid}
     * @param id
     * @return
     */
    public SpOrder getPaidOrder(String id) throws InterruptedException {
        int index = 0;
        int paymentState = 2;
        int[] millsArr = new int[]{1000, 3000, 5000, 7000, 9000};

        SpOrder order = orderDao.findByIdAndPaymentState(id, paymentState);
        while (index < millsArr.length) {
            if (order != null && order.isValid() && order.getPaymentState() == paymentState) {
                log.info("got the paid order");
                return order;
            }
            log.info("getting the paid order...");
            Thread.sleep(millsArr[index]);
            order = orderDao.findByIdAndPaymentState(id, paymentState);
            index++;
        }
        log.warn("get the paid order failed,services are busy,try it later");
        return null;
    }

    public void saveAll(Iterable<SpOrder> entities) {
        orderDao.saveAll(entities);
    }
}
