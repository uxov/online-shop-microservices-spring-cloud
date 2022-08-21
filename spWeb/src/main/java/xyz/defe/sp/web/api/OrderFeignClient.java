package xyz.defe.sp.web.api;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import xyz.defe.sp.common.entity.spOrder.SpOrder;
import xyz.defe.sp.common.pojo.Cart;
import xyz.defe.sp.common.pojo.ResponseData;

@FeignClient(name = "orderClient", url = "${gateway.url}/orderService/")
public interface OrderFeignClient {
    @GetMapping("order/token")
    ResponseData<String> getOrderToken();

    @PostMapping("order")
    ResponseData<SpOrder> newOrder(@RequestBody Cart cart);

    @GetMapping("order/{id}")
    ResponseData<SpOrder> getOrder(@PathVariable String id);

    @GetMapping("order/paid/{id}")
    ResponseData<SpOrder> getPaidOrder(@PathVariable String id);
}
