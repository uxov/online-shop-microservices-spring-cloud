package xyz.defe.sp.test.feignClient.api;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import xyz.defe.sp.common.entity.spProduct.Product;
import xyz.defe.sp.common.pojo.ResponseData;

import java.util.List;

//request to gateway
@FeignClient(name = "apiClient", url = "http://localhost:9000/")
public interface Api {
    @GetMapping("productService/products")
    ResponseData<List<Product>> getProducts(@RequestParam int pageNum, @RequestParam int pageSize);
}
