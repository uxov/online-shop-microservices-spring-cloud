package xyz.defe.sp.order.test;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import xyz.defe.sp.order.service.ProductService;

@SpringBootTest
public class OrderServiceTest {
    @Autowired
    private ProductService productService;

    @Test
    public void returnProducts() throws Exception {
        productService.processExpiredOrders();
    }

}
