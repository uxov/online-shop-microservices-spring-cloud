package xyz.defe.sp.test.feignClient;

import org.assertj.core.util.Strings;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import xyz.defe.sp.common.entity.spProduct.Product;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@SpringBootTest
public class ProductServiceTest {
    @Autowired
    private ProductService productService;

    @Test
    void getProducts() {
        List<Product> list = productService.getProducts(1, 10).getData();
        Assertions.assertNotNull(list);
        Assertions.assertEquals(3, list.size());
        Assertions.assertTrue(!Strings.isNullOrEmpty(list.get(0).getName()));

        int count = 2;
        Set<String> productsIdSet = new HashSet();
        list.stream().limit(count).forEach(p -> productsIdSet.add(p.getId()));
        String ids = String.join(",", productsIdSet);

        List<Product> products = productService.getByIds(ids).getData();
        Assertions.assertEquals(count, products.size());
    }
}
