package xyz.defe.sp.test.restTemplate.services.spOrder;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import xyz.defe.sp.common.entity.spOrder.SpOrder;
import xyz.defe.sp.common.pojo.Cart;
import xyz.defe.sp.common.pojo.ResponseData;
import xyz.defe.sp.common.rest.RestUtil;

@Component
public class OrderRequest {
    @Autowired
    private RestTemplate rest;
    private final String baseURL = "http://localhost:9300/orderService/";

    public ResponseData<String> getOrderToken() {
        return RestUtil.INSTANCE.set(rest).get(baseURL + "order/token");
    }

    public ResponseData<SpOrder> submitOrder(Cart cart) {
        return RestUtil.INSTANCE.set(rest)
                .post(baseURL + "order", cart, new ParameterizedTypeReference<ResponseData<SpOrder>>() {});
    }

    public ResponseData<SpOrder> getToPayOrder(String orderId) {
       return RestUtil.INSTANCE.set(rest)
               .get(baseURL + "order/toPay/{orderId}",
                       new ParameterizedTypeReference<ResponseData<SpOrder>>(){}, orderId);
    }

}
