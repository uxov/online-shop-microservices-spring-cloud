package xyz.defe.sp.payment.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
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
    public Object pay(@RequestHeader("uid") String uid, String orderId) {
        return paymentService.checkAndPay(uid, orderId);
    }

    @GetMapping("wallet")
    public Object getWallet(String uid) {
        return paymentService.getWallet(uid);
    }

}
