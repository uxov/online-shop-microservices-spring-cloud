package xyz.defe.sp.test.restTemplate.services.spOrder;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import xyz.defe.sp.common.entity.spOrder.SpOrder;
import xyz.defe.sp.common.pojo.Cart;
import xyz.defe.sp.common.pojo.ResponseData;
import xyz.defe.sp.common.rest.RestUtil;
import xyz.defe.sp.test.BaseTest;

import java.util.List;

@Component
public class OrderRequest extends BaseTest {
    @Autowired
    private RestTemplate rest;
    private final String baseURL = "http://localhost:9300/orderService/";

    public String getOrderToken() {
        ResponseData<String> responseData = request(() -> {
            return RestUtil.INSTANCE.set(rest).get(baseURL + "order/token");
        });
        return responseData.getData();
    }

    public SpOrder submitOrder(Cart cart) {
        ResponseData<SpOrder> responseData  = request(() -> {
            return RestUtil.INSTANCE.set(rest)
                    .post(baseURL + "order", cart, new ParameterizedTypeReference<ResponseData<SpOrder>>() {});
        });
        return responseData.getData();
    }

   public List<SpOrder> getUnpaidOrders(String uid) {
       ResponseData<List<SpOrder>> responseData = request(() -> {
           return RestUtil.INSTANCE.set(rest)
                   .get(baseURL + "order/unpaid?uid={uid}",
                           new ParameterizedTypeReference<ResponseData<List<SpOrder>>>(){}, uid);
       });
       return responseData.getData();
   }

}
