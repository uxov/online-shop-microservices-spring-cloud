package xyz.defe.sp.test.feignClient;

import com.google.common.base.Strings;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import xyz.defe.sp.common.entity.spOrder.SpOrder;
import xyz.defe.sp.common.entity.spProduct.Product;
import xyz.defe.sp.common.entity.spUser.Account;
import xyz.defe.sp.common.pojo.Cart;

import java.util.List;

@SpringBootTest
public class OrderServiceTest {
    @Autowired
    private UserService userService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private ProductService productService;

    @Test
    void getOrderToken() {
        String orderToken = orderService.getOrderToken().getData();
        Assertions.assertTrue(!Strings.isNullOrEmpty(orderToken));
        System.out.println("orderToken="+orderToken);
    }

    @Test
    void submitOrder() {
        Account account = userService.verify("mike", "123").getData();
        Assertions.assertNotNull(account);

        String orderToken = orderService.getOrderToken().getData();
        Assertions.assertTrue(!org.assertj.core.util.Strings.isNullOrEmpty(orderToken));

        List<Product> products = productService.getProducts(1, 10).getData();
        Assertions.assertNotNull(products);
        Assertions.assertTrue(!products.isEmpty());

        Cart cart = new Cart();
        cart.setUid(account.getId());
        cart.setOrderToken(orderToken);
        cart.getCounterMap().put(products.get(0).getId(), 1);
        cart.getCounterMap().put(products.get(1).getId(), 2);
        SpOrder order = orderService.newOrder(cart).getData();
        Assertions.assertNotNull(order);
        Assertions.assertNotNull(order.getId());
    }
}
