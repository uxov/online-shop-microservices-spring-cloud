package xyz.defe.sp.test.restTemplate.services.spProduct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import xyz.defe.sp.common.entity.spProduct.Product;
import xyz.defe.sp.common.pojo.ResponseData;
import xyz.defe.sp.common.rest.RestUtil;

import java.util.List;

@Component
public class ProductRequest {
    @Autowired
    private RestTemplate rest;
    final String baseURL = "http://localhost:9100/productService/";

    public ResponseData<List<Product>> getProducts() {
        return RestUtil.INSTANCE.set(rest)
                .get(baseURL + "products?pageNum={pageNum}&pageSize={pageSize}",
                        new ParameterizedTypeReference<ResponseData<List<Product>>>(){}, 1, 10);
    }

    public ResponseData addProducts(List<Product> products) {
        return RestUtil.INSTANCE.set(rest).post(baseURL + "products", products);
    }

}