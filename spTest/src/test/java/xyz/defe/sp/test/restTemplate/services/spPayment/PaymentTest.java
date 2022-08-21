package xyz.defe.sp.test.restTemplate.services.spPayment;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import xyz.defe.sp.common.entity.spOrder.SpOrder;
import xyz.defe.sp.common.entity.spPayment.PaymentLog;
import xyz.defe.sp.common.entity.spUser.Account;
import xyz.defe.sp.test.restTemplate.services.spOrder.OrderRequest;
import xyz.defe.sp.test.restTemplate.services.spUser.SpUserRequest;

import java.util.List;

@SpringBootTest
public class PaymentTest {
    @Autowired
    private SpUserRequest spUserRequest;
    @Autowired
    private OrderRequest orderRequest;
    @Autowired
    private PaymentRequest paymentRequest;

    @Test
    public void pay() {
        Account user = spUserRequest.verify("mike", "123");
        List<SpOrder> orderList = orderRequest.getUnpaidOrders(user.getId());
        Assertions.assertNotNull(orderList);
        Assertions.assertTrue(!orderList.isEmpty());
        String orderId = orderList.get(0).getId();
        PaymentLog record = paymentRequest.pay(orderId);
        Assertions.assertNotNull(record);
        Assertions.assertEquals(orderId, record.getOrderId());
    }

}
