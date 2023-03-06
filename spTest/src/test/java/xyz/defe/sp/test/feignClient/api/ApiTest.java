package xyz.defe.sp.test.feignClient.api;

import org.assertj.core.util.Strings;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import xyz.defe.sp.common.entity.spProduct.Product;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

//test request to gateway
@SpringBootTest
public class ApiTest {
    @Autowired
    private Api api;

    @Test
    void getProducts() {
        List<Product> list = api.getProducts(1, 10).getData();
        assertTrue(!Strings.isNullOrEmpty(list.get(0).getName()));
    }
}
