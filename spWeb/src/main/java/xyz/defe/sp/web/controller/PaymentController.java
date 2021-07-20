package xyz.defe.sp.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import xyz.defe.sp.common.pojo.ResponseData;
import xyz.defe.sp.web.service.PaymentService;

@RestController
@RequestMapping("payment")
public class PaymentController extends BaseController {
    @Autowired
    private PaymentService paymentService;

    @PostMapping("pay")
    public ResponseData pay(String orderId) {
        return response(() -> paymentService.pay(orderId));
    }
}
