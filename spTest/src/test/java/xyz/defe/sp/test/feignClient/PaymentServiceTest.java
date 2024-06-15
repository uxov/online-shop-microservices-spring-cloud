package xyz.defe.sp.test.feignClient;

import org.assertj.core.util.Strings;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import xyz.defe.sp.common.entity.spOrder.SpOrder;
import xyz.defe.sp.common.entity.spPayment.PaymentLog;
import xyz.defe.sp.common.entity.spProduct.Product;
import xyz.defe.sp.common.entity.spUser.Account;
import xyz.defe.sp.common.pojo.Cart;
import xyz.defe.sp.test.Users;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class PaymentServiceTest {
    @Autowired
    private UserService userService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private ProductService productService;
    @Autowired
    private PaymentService paymentService;


    @Test
    public void pay() throws InterruptedException {
        Account user = userService.verify(Users.MIKE.uname, Users.MIKE.pwd).getData();
        assertEquals(Users.MIKE.uname, user.getUname());

        String orderToken = orderService.getOrderToken().getData();
        assertTrue(!Strings.isNullOrEmpty(orderToken));

        List<Product> products = productService.getProducts(1, 10).getData();
        assertTrue(!products.isEmpty());

        Cart cart = new Cart();
        cart.setUid(user.getId());
        cart.setOrderToken(orderToken);
        cart.getCounterMap().put(products.get(0).getId(), 1);
        cart.getCounterMap().put(products.get(1).getId(), 2);
        SpOrder order = orderService.newOrder(cart).getData();
        assertTrue(!Strings.isNullOrEmpty(order.getId()));

        Thread.sleep(300);

        SpOrder toPayOrder = orderService.getToPayOrder(order.getId()).getData();
        assertTrue(toPayOrder.isValid());
        assertEquals(order.getId(), toPayOrder.getId());

        PaymentLog paymentLog = paymentService.pay(user.getId(), toPayOrder.getId()).getData();
        assertEquals(toPayOrder.getId(), paymentLog.getOrderId());
    }
}
