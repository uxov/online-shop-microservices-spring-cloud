package xyz.defe.sp.payment.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import xyz.defe.sp.common.entity.spPayment.Wallet;
import xyz.defe.sp.common.response.ResponseDataResult;
import xyz.defe.sp.payment.service.PaymentService;

@RestController
@ResponseDataResult
public class PaymentController {
    @Autowired
    private PaymentService paymentService;

    @PostMapping("wallet")
    public Object createUserWallet(@RequestBody Wallet wallet) {
        return paymentService.createUserWallet(wallet);
    }

    @PostMapping("pay")
    public Object pay(String orderId) {
        return paymentService.pay(orderId);
    }

}
