package xyz.defe.sp.payment.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import xyz.defe.sp.common.ResponseWrap;
import xyz.defe.sp.common.entity.spPayment.Wallet;
import xyz.defe.sp.common.pojo.ResponseData;
import xyz.defe.sp.payment.service.PaymentService;

@RestController
public class PaymentController {
    @Autowired
    private ResponseWrap response;
    @Autowired
    private PaymentService paymentService;

    @PostMapping("wallet")
    public ResponseData createUserWallet(@RequestBody Wallet wallet) {
        return response.wrap(() -> paymentService.createUserWallet(wallet));
    }

    @PostMapping("pay")
    public ResponseData pay(String orderId) throws Exception {
        return response.wrap(() -> paymentService.pay(orderId));
    }

}
