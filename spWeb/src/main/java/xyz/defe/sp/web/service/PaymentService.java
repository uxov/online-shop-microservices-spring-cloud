package xyz.defe.sp.web.service;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import xyz.defe.sp.common.entity.spPayment.PaymentLog;
import xyz.defe.sp.common.pojo.ResponseData;
import xyz.defe.sp.common.rest.RestUtil;

@Service
public class PaymentService extends BaseService {

    public PaymentLog pay(String orderId) {
        ResponseData<PaymentLog> responseData = request(() -> {
            return RestUtil.INSTANCE.set(rest)
                    .post(baseURL + "paymentService/pay?orderId={orderId}",
                            new ParameterizedTypeReference<ResponseData<PaymentLog>>() {}, orderId);
        });
        return responseData.getData();
    }

}
