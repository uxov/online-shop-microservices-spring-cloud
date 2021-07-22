package xyz.defe.sp.test.services.spPayment;

import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import xyz.defe.sp.common.entity.spPayment.PaymentLog;
import xyz.defe.sp.common.entity.spPayment.Wallet;
import xyz.defe.sp.common.pojo.ResponseData;
import xyz.defe.sp.common.rest.RestUtil;
import xyz.defe.sp.test.BaseTest;

@Component
public class PaymentRequest extends BaseTest {
    @Autowired
    private Gson gson;
    @Autowired
    private RestTemplate rest;
    private final String baseURL = "http://localhost:9004/paymentService/";

    public PaymentLog pay(String orderId) {
        ResponseData<PaymentLog> responseData = request(() -> {
            return RestUtil.INSTANCE.set(rest).post(baseURL + "pay?orderId={orderId}",
                            new ParameterizedTypeReference<ResponseData<PaymentLog>>() {}, orderId);
        });
        return responseData.getData();
    }

    public Wallet createUserWallet(Wallet wallet) {
        ResponseData<Wallet> responseData = RestUtil.INSTANCE.set(rest)
                .post(baseURL + "wallet", wallet, new ParameterizedTypeReference<ResponseData<Wallet>>() {});
        return responseData.getData();
    }
}
