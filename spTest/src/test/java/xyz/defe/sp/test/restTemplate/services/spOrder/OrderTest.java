package xyz.defe.sp.test.restTemplate.services.spOrder;

import org.assertj.core.util.Strings;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import xyz.defe.sp.common.entity.spOrder.SpOrder;
import xyz.defe.sp.common.entity.spProduct.Product;
import xyz.defe.sp.common.entity.spUser.Account;
import xyz.defe.sp.common.pojo.Cart;
import xyz.defe.sp.test.Users;
import xyz.defe.sp.test.restTemplate.services.spProduct.ProductRequest;
import xyz.defe.sp.test.restTemplate.services.spUser.SpUserRequest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class OrderTest {
    @Autowired
    private SpUserRequest spUserRequest;
    @Autowired
    private ProductRequest productRequest;
    @Autowired
    private OrderRequest orderRequest;

    @Test
    void getOrderToken() {
        String orderToken = orderRequest.getOrderToken().getData();
        assertTrue(!Strings.isNullOrEmpty(orderToken));
    }

    @Test
    void submitOrder() {
        Account user = spUserRequest.verify(Users.MIKE.uname, Users.MIKE.pwd).getData();
        assertEquals(Users.MIKE.uname, user.getUname());

        String orderToken = orderRequest.getOrderToken().getData();
        assertTrue(!Strings.isNullOrEmpty(orderToken));

        List<Product> products = productRequest.getProducts().getData();
        assertTrue(!products.isEmpty());

        Cart cart = new Cart();
        cart.setUid(user.getId());
        cart.setOrderToken(orderToken);
        cart.getCounterMap().put(products.get(0).getId(), 1);
        cart.getCounterMap().put(products.get(1).getId(), 2);
        SpOrder order = orderRequest.submitOrder(cart).getData();
        assertTrue(!Strings.isNullOrEmpty(order.getId()));
    }

}
