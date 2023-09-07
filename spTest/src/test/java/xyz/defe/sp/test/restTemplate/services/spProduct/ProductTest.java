package xyz.defe.sp.test.restTemplate.services.spProduct;

import org.assertj.core.util.Strings;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import xyz.defe.sp.common.entity.spProduct.Product;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class ProductTest {
    @Autowired
    private ProductRequest productRequest;

    @Test
    void getProducts() {
        List<Product> list = productRequest.getProducts().getData();
//        assertTrue(!list.isEmpty());
        assertTrue(!Strings.isNullOrEmpty(list.get(0).getName()));
        list.forEach(p -> assertTrue(p.getQuantity() > 0));
    }

}
