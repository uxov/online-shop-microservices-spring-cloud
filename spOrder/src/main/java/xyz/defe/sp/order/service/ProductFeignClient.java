package xyz.defe.sp.order.service;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import xyz.defe.sp.common.entity.spProduct.Product;
import xyz.defe.sp.common.pojo.ResponseData;

import java.util.List;
import java.util.Map;
import java.util.Set;

@FeignClient(value = "${sp-product-service.url}")
public interface ProductFeignClient {
    @GetMapping("products/{ids}")
    ResponseData<List<Product>> getByIds(@PathVariable("ids") String ids);

    @PostMapping("quantity/restore")
    ResponseData<Set<String>> restoreProductQuantity(@RequestBody Map<String, Map<String, Integer>> restoreMap);
}
