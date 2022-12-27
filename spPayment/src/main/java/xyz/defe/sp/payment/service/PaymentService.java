package xyz.defe.sp.payment.service;

import xyz.defe.sp.common.entity.spPayment.PaymentLog;
import xyz.defe.sp.common.entity.spPayment.Wallet;

public interface PaymentService {
    Wallet createUserWallet(Wallet wallet);

    PaymentLog checkAndPay(String uid, String orderId);

    PaymentLog pay(String orderId);

    Wallet getWallet(String uid);
}
