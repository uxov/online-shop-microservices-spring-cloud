package xyz.defe.sp.payment.service;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import xyz.defe.sp.common.entity.spOrder.SpOrder;
import xyz.defe.sp.common.pojo.ResponseData;

@FeignClient(value = "${sp-order-service.url}")
public interface OrderFeignClient {
    @GetMapping("order/{id}")
    ResponseData<SpOrder> getOrder(@PathVariable String id);

    @GetMapping("order/toPay/{id}")
    ResponseData<SpOrder> getToPayOrder(@PathVariable String id);
}
