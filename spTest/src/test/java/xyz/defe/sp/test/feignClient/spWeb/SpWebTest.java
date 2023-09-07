package xyz.defe.sp.test.feignClient.spWeb;

import org.assertj.core.util.Strings;
import org.junit.jupiter.api.RepeatedTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import xyz.defe.sp.common.entity.spOrder.SpOrder;
import xyz.defe.sp.common.entity.spPayment.PaymentLog;
import xyz.defe.sp.common.entity.spProduct.Product;
import xyz.defe.sp.common.pojo.Cart;
import xyz.defe.sp.test.Users;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

//test request to spWeb
@SpringBootTest
public class SpWebTest {
    @Autowired
    private SpWeb spWeb;

    @RepeatedTest(1)
    public void request() {
        //1. get products
        List<Product> products = spWeb.getProducts(1, 10).getData();
        assertTrue(!products.isEmpty());
        products.forEach(p -> assertTrue(p.getQuantity() > 0));

        //2. user login
        Map<String, String> map = spWeb.login(Users.MIKE.uname, Users.MIKE.pwd).getData();
        String uid = map.get("uid");
        String token = map.get("token");
        assertTrue(!Strings.isNullOrEmpty(uid));
        assertTrue(!Strings.isNullOrEmpty(token));

        //3. add products to cart and submit the order
        String orderToken = spWeb.getOrderToken(token).getData();
        assertTrue(!Strings.isNullOrEmpty(orderToken));
        Cart cart = new Cart();
        cart.setUid(uid);
        cart.setOrderToken(orderToken);
        cart.getCounterMap().put(products.get(0).getId(), 1);
        cart.getCounterMap().put(products.get(1).getId(), 2);
        SpOrder order = spWeb.newOrder(cart, token).getData();
        assertTrue(order.isValid());

        //4. pay the order
        PaymentLog record = spWeb.pay(order.getId(), token).getData();
        assertEquals(order.getId(), record.getOrderId());

        //5. get the paid order
        order = spWeb.getPaidOrder(order.getId(), token).getData();
        assertEquals(2, order.getPaymentState());
    }
}
