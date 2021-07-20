package xyz.defe.sp.web.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import xyz.defe.sp.common.RestUtil;
import xyz.defe.sp.common.entity.spOrder.SpOrder;
import xyz.defe.sp.common.pojo.Cart;
import xyz.defe.sp.common.pojo.ResponseData;

@Service
public class OrderService extends BaseService {
    @Autowired
    private ProductService productService;
    @Autowired
    private OrderService orderService;

    public String getOrderToken() throws Exception {
        ResponseData<String> responseData = request(() -> {
            return RestUtil.INSTANCE.set(rest)
                    .get(baseURL + "orderService/order/token");
        });
        return responseData.getData();
    }

    public SpOrder newOrder(Cart cart) throws Exception {
        ResponseData<SpOrder> responseData  = request(() -> {
            return RestUtil.INSTANCE.set(rest)
                    .post(baseURL + "orderService/order", cart,
                            new ParameterizedTypeReference<ResponseData<SpOrder>>() {});
        });
        return responseData.getData();
    }

    public SpOrder getOrder(String orderId) throws Exception {
        ResponseData<SpOrder> responseData = request(() -> {
            return RestUtil.INSTANCE.set(rest)
                    .get(baseURL + "orderService/order/{id}",
                            new ParameterizedTypeReference<ResponseData<SpOrder>>() {}, orderId);
        });
        return responseData.getData();
    }

    public SpOrder getPaidOrder(String orderId) throws Exception {
        ResponseData<SpOrder> responseData = request(() -> {
            return RestUtil.INSTANCE.set(rest)
                    .get(baseURL + "orderService/order/paid/{id}",
                            new ParameterizedTypeReference<ResponseData<SpOrder>>() {}, orderId);
        });
        return responseData.getData();
    }

}
