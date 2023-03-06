package xyz.defe.sp.test.restTemplate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import xyz.defe.sp.common.entity.spOrder.SpOrder;
import xyz.defe.sp.common.entity.spPayment.PaymentLog;
import xyz.defe.sp.common.entity.spProduct.Product;
import xyz.defe.sp.common.pojo.Cart;
import xyz.defe.sp.common.pojo.ResponseData;
import xyz.defe.sp.common.rest.RestUtil;

import java.util.List;
import java.util.Map;

@Component
public class SpWebRest {
    @Autowired
    private RestTemplate rest;
    private final String baseURL = "http://localhost:8000/";  //spWeb URL

    public ResponseData<List<Product>> getProducts(int pageNum, int pageSize) {
        return RestUtil.INSTANCE.set(rest)
                .get(baseURL + "products?pageNum={pageNum}&pageSize={pageSize}",
                        new ParameterizedTypeReference<ResponseData<List<Product>>>(){}, pageNum, pageSize);
    }

    public ResponseData<Map> login(String uname, String pwd) {
        Map<String, String> paramMap = Map.of("uname", uname, "pwd", pwd);
        return RestUtil.INSTANCE.set(rest)
                .post(baseURL + "login", paramMap, new ParameterizedTypeReference<ResponseData<Map>>() {});

    }

    public ResponseData getOrderToken() {
        return RestUtil.INSTANCE.set(rest).get(baseURL + "order/orderToken");
    }

    public ResponseData<SpOrder> submitOrder(Cart cart) {
        return RestUtil.INSTANCE.set(rest)
                .post(baseURL + "order", cart,
                        new ParameterizedTypeReference<ResponseData<SpOrder>>() {});
    }

    public ResponseData<PaymentLog> pay(String orderId) {
        return RestUtil.INSTANCE.set(rest)
                .post(baseURL + "payment/pay?orderId={orderId}",
                        new ParameterizedTypeReference<ResponseData<PaymentLog>>() {}, orderId);
    }

    public ResponseData<SpOrder> getPaidOrder(String orderId) {
        return RestUtil.INSTANCE.set(rest)
                .get(baseURL + "order/paid/{id}",
                        new ParameterizedTypeReference<ResponseData<SpOrder>>() {}, orderId);
    }
}
