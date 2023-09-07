package xyz.defe.sp.test.restTemplate;

import com.google.common.base.Strings;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import xyz.defe.sp.common.entity.spOrder.SpOrder;
import xyz.defe.sp.common.entity.spPayment.PaymentLog;
import xyz.defe.sp.common.entity.spProduct.Product;
import xyz.defe.sp.common.pojo.Cart;
import xyz.defe.sp.test.Users;
import xyz.defe.sp.test.config.HeaderConfig;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

;

@SpringBootTest
public class SpWebRestTest {
    @Autowired
    private SpWebRest spWebRest;

    @Test
    public void request() {
        HeaderConfig.token = "";

        //1. get products
        List<Product> products = spWebRest.getProducts(1, 10).getData();
        assertTrue(!products.isEmpty());
        products.forEach(p -> assertTrue(p.getQuantity() > 0));

        //2. user login
        Map<String, String> map = spWebRest.login(Users.MIKE.uname, Users.MIKE.pwd).getData();
        String uid = map.get("uid");
        String token = map.get("token");
        assertTrue(!Strings.isNullOrEmpty(uid));
        assertTrue(!Strings.isNullOrEmpty(token));
        HeaderConfig.token = token;

        //3. add products to cart and submit the order
        String orderToken = (String) spWebRest.getOrderToken().getData();
        assertTrue(!Strings.isNullOrEmpty(orderToken));
        Cart cart = new Cart();
        cart.setUid(uid);
        cart.setOrderToken(orderToken);
        cart.getCounterMap().put(products.get(0).getId(), 1);
        cart.getCounterMap().put(products.get(1).getId(), 2);
        SpOrder order = spWebRest.submitOrder(cart).getData();
        assertTrue(!Strings.isNullOrEmpty(order.getId()));

        //4. pay the order
        PaymentLog paymentLog = spWebRest.pay(order.getId()).getData();
        assertEquals(order.getId(), paymentLog.getOrderId());

        //5. get the paid order
        SpOrder paidOrder = spWebRest.getPaidOrder(order.getId()).getData();
        assertEquals(2, paidOrder.getPaymentState());
    }
}
