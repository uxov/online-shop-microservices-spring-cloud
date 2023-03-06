package xyz.defe.sp.test.restTemplate.services.spPayment;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import xyz.defe.sp.common.entity.spPayment.PaymentLog;
import xyz.defe.sp.common.entity.spPayment.Wallet;
import xyz.defe.sp.common.pojo.ResponseData;
import xyz.defe.sp.common.rest.RestUtil;
import xyz.defe.sp.test.config.HeaderConfig;

@Component
public class PaymentRequest {
    @Autowired
    private RestTemplate rest;
    private final String baseURL = "http://localhost:9400/paymentService/";

    public ResponseData<PaymentLog> pay(String uid, String orderId) {
        HeaderConfig.uid = uid;
        return RestUtil.INSTANCE.set(rest).post(
                baseURL + "pay?orderId={orderId}",
                new ParameterizedTypeReference<ResponseData<PaymentLog>>() {}, orderId);
    }

    public ResponseData<Wallet> createUserWallet(Wallet wallet) {
        return RestUtil.INSTANCE.set(rest)
                .post(baseURL + "wallet", wallet, new ParameterizedTypeReference<ResponseData<Wallet>>() {});
    }

}
