package xyz.defe.sp.payment.service;

import com.google.common.base.Strings;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import xyz.defe.sp.common.Const;
import xyz.defe.sp.common.ExceptionUtil;
import xyz.defe.sp.common.WarnException;
import xyz.defe.sp.common.entity.spOrder.SpOrder;
import xyz.defe.sp.common.entity.spPayment.PaymentLog;
import xyz.defe.sp.common.entity.spPayment.Wallet;
import xyz.defe.sp.common.entity.spProduct.Product;
import xyz.defe.sp.common.pojo.Cart;
import xyz.defe.sp.common.pojo.OrderMsg;
import xyz.defe.sp.payment.dao.PaymentLogDao;
import xyz.defe.sp.payment.dao.WalletDao;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.util.Set;

@Service
public class PaymentServiceImpl implements PaymentService {
    @Autowired
    private Gson gson;
    @Autowired
    private WalletDao walletDao;
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

    private void checkOrder(String orderId) throws Exception {
        if (Strings.isNullOrEmpty(orderId)) {ExceptionUtil.warn("orderId is empty");}
        PaymentLog record = paymentLogDao.findByOrderId(orderId);
        if (record != null) {ExceptionUtil.warn("the order is paid,id="+ orderId);}
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public PaymentLog pay(String orderId) throws Exception {
        checkOrder(orderId);
        SpOrder order = orderService.getToPayOrder(orderId);
        if (order == null) {ExceptionUtil.warn("the order is not able to pay,id=" + orderId);}

        //when the order is valid and paymentState=1 then process
        //get user's wallet
        Wallet wallet = walletDao.findByUserId(order.getUserId());
        if (null == wallet) {
            throw new WarnException("user's wallet not exists,uid=" + order.getUserId());
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
            throw new WarnException("user's balance is not enough to pay the order; cost=" + sum
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
        log.info("send message to ORDER SERVICE - to set order paid");

        return record;
    }


}
