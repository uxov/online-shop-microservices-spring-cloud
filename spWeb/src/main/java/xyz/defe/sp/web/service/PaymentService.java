package xyz.defe.sp.web.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import xyz.defe.sp.common.entity.spPayment.PaymentLog;
import xyz.defe.sp.common.pojo.ResponseData;
import xyz.defe.sp.web.api.PaymentServiceFeignClient;

@Service
public class PaymentService {
    @Autowired
    private PaymentServiceFeignClient paymentServiceFeignClient;

    public ResponseData<PaymentLog> pay(String orderId) {
        return paymentServiceFeignClient.pay(orderId);
    }

}
