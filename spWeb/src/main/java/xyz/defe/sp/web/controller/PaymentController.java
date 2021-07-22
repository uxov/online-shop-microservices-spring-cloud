package xyz.defe.sp.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import xyz.defe.sp.common.response.ResponseDataResult;
import xyz.defe.sp.web.service.PaymentService;

@RestController
@ResponseDataResult
@RequestMapping("payment")
public class PaymentController {
    @Autowired
    private PaymentService paymentService;

    @PostMapping("pay")
    public Object pay(String orderId) {
        return paymentService.pay(orderId);
    }
}
