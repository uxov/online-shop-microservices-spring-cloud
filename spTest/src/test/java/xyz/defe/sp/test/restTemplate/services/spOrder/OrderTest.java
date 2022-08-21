package xyz.defe.sp.test.restTemplate.services.spOrder;

import org.assertj.core.util.Strings;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import xyz.defe.sp.common.entity.spOrder.SpOrder;
import xyz.defe.sp.common.entity.spProduct.Product;
import xyz.defe.sp.common.entity.spUser.Account;
import xyz.defe.sp.common.pojo.Cart;
import xyz.defe.sp.test.restTemplate.services.spUser.SpUserRequest;
import xyz.defe.sp.test.restTemplate.services.spProduct.ProductRequest;

import java.util.List;

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
        String orderToken = orderRequest.getOrderToken();
        Assertions.assertTrue(!Strings.isNullOrEmpty(orderToken));
        System.out.println("orderToken="+orderToken);
    }

    @Test
    void submitOrder() {
        Account account = spUserRequest.verify("mike", "123");
        Assertions.assertNotNull(account);

        String orderToken = orderRequest.getOrderToken();
        Assertions.assertTrue(!Strings.isNullOrEmpty(orderToken));

        List<Product> products = productRequest.getProducts();
        Assertions.assertNotNull(products);
        Assertions.assertTrue(!products.isEmpty());

        Cart cart = new Cart();
        cart.setUid(account.getId());
        cart.setOrderToken(orderToken);
        cart.getCounterMap().put(products.get(0).getId(), 1);
        cart.getCounterMap().put(products.get(1).getId(), 2);
        SpOrder order = orderRequest.submitOrder(cart);
        Assertions.assertNotNull(order);
        Assertions.assertNotNull(order.getId());
    }

}
