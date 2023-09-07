package xyz.defe.sp.test.feignClient;

import org.assertj.core.util.Strings;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import xyz.defe.sp.common.entity.spProduct.Product;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

;

@SpringBootTest
public class ProductServiceTest {
    @Autowired
    private ProductService productService;

    @Test
    void getProducts() {
        List<Product> list = productService.getProducts(1, 10).getData();
        assertTrue(!Strings.isNullOrEmpty(list.get(0).getName()));
        list.forEach(p -> assertTrue(p.getQuantity() > 0));

        int count = 2;
        Set<String> productIdSet = list.stream().limit(count).map(e -> e.getId())
                .collect(Collectors.toSet());
        List<Product> products = productService.getByIds(String.join(",", productIdSet)).getData();
        assertEquals(count, products.size());
    }

    @Test
    void getQuantity() {
        List<Product> list = productService.getProducts(1, 10).getData();
        Set<String> productIdSet = list.stream().map(e -> e.getId())
                .collect(Collectors.toSet());
        Map<String, Integer> map =  productService
                .getQuantity(String.join(",", productIdSet)).getData();
        assertTrue(!map.isEmpty());
//        map.entrySet().forEach(e -> System.out.println(e.getKey() + " : " + e.getValue()));
        list.forEach(p -> {
            assertEquals(p.getQuantity(), map.get(p.getId()));
        });
    }
}
