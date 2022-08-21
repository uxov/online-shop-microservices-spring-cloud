package xyz.defe.sp.test.restTemplate.services.spProduct;

import org.assertj.core.util.Strings;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import xyz.defe.sp.common.entity.spProduct.Product;

import java.util.List;

@SpringBootTest
public class ProductTest {
    @Autowired
    private ProductRequest productRequest;

    @Test
    void getProducts() {
        List<Product> list = productRequest.getProducts();
        Assertions.assertNotNull(list);
        Assertions.assertEquals(3, list.size());
        Assertions.assertTrue(!Strings.isNullOrEmpty(list.get(0).getName()));
    }

}
