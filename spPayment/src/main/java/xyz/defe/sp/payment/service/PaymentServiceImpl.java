package xyz.defe.sp.payment.service;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import xyz.defe.sp.common.Const;
import xyz.defe.sp.common.entity.spOrder.SpOrder;
import xyz.defe.sp.common.entity.spPayment.PaymentLog;
import xyz.defe.sp.common.entity.spPayment.Wallet;
import xyz.defe.sp.common.entity.spProduct.Product;
import xyz.defe.sp.common.exception.ExceptionUtil;
import xyz.defe.sp.common.pojo.Cart;
import xyz.defe.sp.common.pojo.OrderMsg;
import xyz.defe.sp.common.util.CheckParam;
import xyz.defe.sp.payment.dao.PaymentLogDao;
import xyz.defe.sp.payment.dao.WalletDao;

import jakarta.transaction.Transactional;
import java.math.BigDecimal;
import java.util.Set;

@Service
public class PaymentServiceImpl implements PaymentService {
    @Autowired
    private Gson gson;
    @Autowired
    private WalletDao walletDao;
    @Autowired
    public RedissonClient redisson;
    @Autowired
    private PaymentLogDao paymentLogDao;
    @Autowired
    private OrderService orderService;
    @Autowired
    private LocalMessageService localMessageService;
    @Autowired
    private MqMessageService mqMessageService;
    final Logger log = LoggerFactory.getLogger(this.getClass());

    @Override
    public Wallet createUserWallet(Wallet wallet) {
        return walletDao.saveAndFlush(wallet);
    }

    @Override
    @Transactional
    public PaymentLog checkAndPay(String uid, String orderId) {
        CheckParam.check(uid, "uid is null or empty");
        CheckParam.check(orderId, "orderId is null or empty");

        RLock lock = getLockByUid(uid);
        lock.lock();
        try {
            PaymentLog record = paymentLogDao.findByOrderId(orderId);
            if (record != null) {
                ExceptionUtil.warn("the order is paid,id=" + orderId);
            }
            return pay(orderId);
        } finally {
            lock.unlock();
        }
    }

    @Override
    @Transactional
    public PaymentLog pay(String orderId) {
        SpOrder order = null;
        try {
            order = orderService.getToPayOrder(orderId).get();
        } catch (Exception e) {
            ExceptionUtil.warn(e.getMessage());
        }
        if (order == null) {ExceptionUtil.warn("the order is not able to pay,id=" + orderId + ",please try it later.");}

        //when the order is valid and paymentState=1 then process
        //get user's wallet
        Wallet wallet = walletDao.findByUserId(order.getUserId());
        if (null == wallet) {
            ExceptionUtil.warn("user's wallet not exists,uid=" + order.getUserId());
        }

        Cart cart = gson.fromJson(order.getCartJson(),
                new TypeToken<Cart>(){}.getType());
        Set<Product> productSet = cart.getProductSet();

        //calculate
        double sum = 0.00;
        int count = 0;
        double price = 0.00;
        for (Product product : productSet) {
            count = cart.getCounterMap().get(product.getId());
            price = product.getPrice().doubleValue();
            sum += price * count;
        }
        double remain = wallet.getBalance().doubleValue() - sum;
        if (remain < 0) {
            ExceptionUtil.warn("user's balance is not enough to pay the order; cost=" + sum
                    + ",balance=" + wallet.getBalance().doubleValue() + ",uid="
                    + wallet.getUserId() + ",order id="+order.getUserId());
        }
        wallet.setBalance(new BigDecimal(remain));
        walletDao.saveAndFlush(wallet);

        //create payment record
        PaymentLog record = new PaymentLog();
        record.setOrderId(order.getId());
        record.setUserId(order.getUserId());
        record.setPayment(new BigDecimal(sum));
        paymentLogDao.saveAndFlush(record);

        //create a message(set order paid) send to ORDER SERVICE
        OrderMsg message = new OrderMsg();
        message.setOrderId(order.getId());
        message.setFrom(Const.PAYMENT_SERVER);
        message.setTo(Const.ORDER_SERVER);
        //save message to local table
        localMessageService.saveOrderMessage(message, 1);
        log.info("pay successful,order id={}", order.getId());
        //send message
        mqMessageService.send(message.getId(), message);

        return record;
    }

    public Wallet getWallet(String uid) {return walletDao.findByUserId(uid);}

    /**
     * to prevent duplicate requests and synchronize control
     * @param uid
     * @return
     */
    private RLock getLockByUid(String uid) {
        return redisson.getLock(Const.USER_LOCK_FOR_PAYMENT_PRE + uid);
    }
}
