package xyz.defe.sp.test.feignClient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import xyz.defe.sp.common.entity.spOrder.SpOrder;
import xyz.defe.sp.common.pojo.Cart;
import xyz.defe.sp.common.pojo.ResponseData;

@FeignClient(name = "orderServiceClient", url = "http://localhost:9300/orderService/")
public interface OrderService {
    @GetMapping("order/token")
    ResponseData<String> getOrderToken();

    @PostMapping("order")
    ResponseData<SpOrder> newOrder(@RequestBody Cart cart);

    @GetMapping("order/toPay/{id}")
    ResponseData<SpOrder> getToPayOrder(@PathVariable String id);
}
