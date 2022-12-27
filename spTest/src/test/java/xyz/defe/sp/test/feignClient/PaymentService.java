package xyz.defe.sp.test.feignClient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import xyz.defe.sp.common.entity.spPayment.PaymentLog;
import xyz.defe.sp.common.pojo.ResponseData;

@FeignClient(name = "paymentServiceClient", url = "http://localhost:9400/paymentService/")
public interface PaymentService {
    @PostMapping("pay")
    ResponseData<PaymentLog> pay(@RequestHeader String uid, @RequestParam String orderId);
}
