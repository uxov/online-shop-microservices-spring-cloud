package xyz.defe.sp.test.feignClient.spWeb;

import org.assertj.core.util.Strings;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import xyz.defe.sp.common.entity.spOrder.SpOrder;
import xyz.defe.sp.common.entity.spPayment.PaymentLog;
import xyz.defe.sp.common.entity.spProduct.Product;
import xyz.defe.sp.common.pojo.Cart;
import xyz.defe.sp.test.config.TokenConfig;

import java.util.List;
import java.util.Map;

//test request to spWeb
@SpringBootTest
public class SpWebTest {
    @Autowired
    private SpWeb spWeb;

    @Test
    public void request() {
        TokenConfig.token = "";

        //a. get products
        List<Product> products = spWeb.getProducts(1, 10).getData();
        Assertions.assertNotNull(products);
        Assertions.assertEquals(3, products.size());

        //b. user login
        Map<String, String> map = spWeb.login("mike", "123").getData();
        String uid = map.get("uid");
        String token = map.get("token");
        Assertions.assertTrue(!Strings.isNullOrEmpty(uid));
        Assertions.assertTrue(!Strings.isNullOrEmpty(token));
        TokenConfig.token = token;

        //c. add products to cart and submit the order
        String orderToken = spWeb.getOrderToken().getData();
        Assertions.assertTrue(!Strings.isNullOrEmpty(orderToken));
        Cart cart = new Cart();
        cart.setUid(uid);
        cart.setOrderToken(orderToken);
        cart.getCounterMap().put(products.get(0).getId(), 1);
        cart.getCounterMap().put(products.get(1).getId(), 2);
        SpOrder order = spWeb.newOrder(cart).getData();
        Assertions.assertNotNull(order);
        String orderId = order.getId();
        Assertions.assertNotNull(orderId);

        //d. pay the order
        PaymentLog record = spWeb.pay(orderId).getData();
        Assertions.assertNotNull(record);
        Assertions.assertEquals(orderId, record.getOrderId());

        //e. get the paid order
        order = spWeb.getPaidOrder(orderId).getData();
        Assertions.assertEquals(2, order.getPaymentState());
    }
}
