package xyz.defe.sp.test.feignClient;

import com.google.common.base.Strings;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import xyz.defe.sp.common.entity.spOrder.SpOrder;
import xyz.defe.sp.common.entity.spPayment.PaymentLog;
import xyz.defe.sp.common.entity.spUser.Account;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class PaymentServiceTest {
    @Autowired
    private UserService userService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private PaymentService paymentService;

    @Test
    public void pay() {
        Account user = userService.verify("mike", "123").getData();
        List<SpOrder> orderList = orderService.getUnpaidOrders(user.getId()).getData();
        assertNotNull(orderList);
        assertTrue(!orderList.isEmpty());
        String orderId = orderList.get(0).getId();
        assertTrue(!Strings.isNullOrEmpty(orderId));
        PaymentLog record = paymentService.pay(user.getId(), orderId).getData();
        assertNotNull(record);
        assertEquals(orderId, record.getOrderId());
    }
}
