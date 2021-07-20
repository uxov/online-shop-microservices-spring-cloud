package xyz.defe.sp.product.test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import xyz.defe.sp.product.service.ProductService;

@SpringBootTest
public class ProductServiceTest {
    @Autowired
    private ProductService productService;

}
