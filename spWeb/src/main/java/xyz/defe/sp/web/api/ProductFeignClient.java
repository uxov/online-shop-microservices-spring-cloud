package xyz.defe.sp.web.api;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import xyz.defe.sp.common.entity.spProduct.Product;
import xyz.defe.sp.common.pojo.ResponseData;

import java.util.List;

@FeignClient(name = "productClient", url = "${gateway.url}/productService/")
public interface ProductFeignClient {
    @GetMapping("products")
    ResponseData<List<Product>> getProducts(@RequestParam int pageNum, @RequestParam int pageSize);
}
