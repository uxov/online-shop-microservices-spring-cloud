package xyz.defe.sp.payment.service;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import xyz.defe.sp.common.entity.spProduct.Product;
import xyz.defe.sp.common.pojo.ResponseData;

import java.util.List;

@FeignClient(value = "${sp-product-service.url}")
public interface ProductFeignClient {
    @GetMapping("products/{ids}")
    ResponseData<List<Product>> getByIds(@PathVariable("ids") String ids);
}
