package xyz.defe.sp.test.services.spProduct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import xyz.defe.sp.common.entity.spProduct.Product;
import xyz.defe.sp.common.pojo.ResponseData;
import xyz.defe.sp.common.rest.RestUtil;
import xyz.defe.sp.test.BaseTest;

import java.util.List;

@Component
public class ProductRequest extends BaseTest {
    @Autowired
    private RestTemplate rest;
    final String baseURL = "http://localhost:9001/productService/";

    public List<Product> getProducts() {
        ResponseData<List<Product>> responseData = request(() -> {
            return RestUtil.INSTANCE.set(rest)
                    .get(baseURL + "products?pageNum={pageNum}&pageSize={pageSize}",
                            new ParameterizedTypeReference<ResponseData<List<Product>>>(){}, 1, 10);
        });
        return responseData.getData();
    }

    public ResponseData addProducts(List<Product> products) {
        return request(() -> {
            return RestUtil.INSTANCE.set(rest).post(baseURL, products);
        });
    }

}