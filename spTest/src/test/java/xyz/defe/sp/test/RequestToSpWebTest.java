package xyz.defe.sp.test;

import org.assertj.core.util.Strings;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.client.RestTemplate;
import xyz.defe.sp.common.entity.spOrder.SpOrder;
import xyz.defe.sp.common.entity.spPayment.PaymentLog;
import xyz.defe.sp.common.entity.spProduct.Product;
import xyz.defe.sp.common.pojo.Cart;
import xyz.defe.sp.common.pojo.ResponseData;
import xyz.defe.sp.common.rest.RestUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SpringBootTest
public class RequestToSpWebTest extends BaseTest {
    @Autowired
    private RestTemplate rest;
    private final String baseURL = "http://localhost:9090/";  //spWeb URL

    @Test
    public void request() {
        //a. get products
        ResponseData responseData = request(() -> {
            return RestUtil.INSTANCE.set(rest)
                    .get(baseURL + "product/list?pageNum={pageNum}&pageSize={pageSize}",
                            new ParameterizedTypeReference<ResponseData<List<Product>>>() {}, 1, 10);
        });
        List<Product> products = (List<Product>) responseData.getData();
        Assertions.assertNotNull(products);
        Assertions.assertEquals(3, products.size());

        //b. user login
        Map<String, String> paramMap = new HashMap();
        paramMap.put("uname", "mike");
        paramMap.put("pwd", "123");
        responseData = request(() -> {
            return RestUtil.INSTANCE.set(rest)
                    .post(baseURL + "login", paramMap, new ParameterizedTypeReference<ResponseData<Map>>() {});
        });
        Map map = (Map) responseData.getData();
        String uid = (String) map.get("uid");
        String token = (String) map.get("token");
        Assertions.assertTrue(!Strings.isNullOrEmpty(uid));
        Assertions.assertTrue(!Strings.isNullOrEmpty(token));

        //c. add products to cart and submit the order
        responseData = request(() -> {
            return RestUtil.INSTANCE.set(rest).get(baseURL + "order/orderToken?token={token}", token);
        });
        String orderToken = (String) responseData.getData();
        Assertions.assertTrue(!Strings.isNullOrEmpty(orderToken));
        Cart cart = new Cart();
        cart.setUid(uid);
        cart.setOrderToken(orderToken);
        cart.getCounterMap().put(products.get(0).getId(), 1);
        cart.getCounterMap().put(products.get(1).getId(), 2);
        responseData  = request(() -> {
            return RestUtil.INSTANCE.set(rest)
                    .post(baseURL + "order/?token={token}", cart,
                            new ParameterizedTypeReference<ResponseData<SpOrder>>() {}, token);
        });
        SpOrder order = (SpOrder) responseData.getData();
        Assertions.assertNotNull(order);
        String orderId = order.getId();
        Assertions.assertNotNull(orderId);

        //d. pay the order
        responseData = request(() -> {
            return RestUtil.INSTANCE.set(rest)
                    .post(baseURL + "payment/pay?token={token}&orderId={orderId}",
                            new ParameterizedTypeReference<ResponseData<PaymentLog>>() {},
                            token, orderId);
        });
        PaymentLog record = (PaymentLog) responseData.getData();
        Assertions.assertNotNull(record);
        Assertions.assertEquals(orderId, record.getOrderId());

        //e. get the paid order
        responseData = request(() -> {
            return RestUtil.INSTANCE.set(rest)
                    .get(baseURL + "order/paid/{id}?token={token}",
                            new ParameterizedTypeReference<ResponseData<SpOrder>>() {}, orderId, token);
        });
        order = (SpOrder) responseData.getData();
        Assertions.assertEquals(2, order.getPaymentState());
    }
}
