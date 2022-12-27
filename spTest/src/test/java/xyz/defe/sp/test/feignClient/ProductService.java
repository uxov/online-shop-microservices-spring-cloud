package xyz.defe.sp.test.feignClient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import xyz.defe.sp.common.entity.spProduct.Product;
import xyz.defe.sp.common.pojo.ResponseData;

import java.util.List;
import java.util.Map;

@FeignClient(name = "productServiceClient", url = "http://localhost:9100/productService/")
public interface ProductService {
    @GetMapping("products")
    ResponseData<List<Product>> getProducts(@RequestParam int pageNum, @RequestParam int pageSize);

    @GetMapping("products/{ids}")
    ResponseData<List<Product>> getByIds(@PathVariable String ids);

    @GetMapping("quantity/{ids}")
    ResponseData<Map<String, Integer>> getQuantity(@PathVariable String ids);
}
