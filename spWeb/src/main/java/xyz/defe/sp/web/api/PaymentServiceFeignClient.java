package xyz.defe.sp.web.api;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import xyz.defe.sp.common.entity.spPayment.PaymentLog;
import xyz.defe.sp.common.pojo.ResponseData;

@FeignClient(name = "paymentServiceClient", url = "${gateway.url}/paymentService/")
public interface PaymentServiceFeignClient {
    @PostMapping("pay")
    ResponseData<PaymentLog> pay(@RequestParam String orderId);
}
