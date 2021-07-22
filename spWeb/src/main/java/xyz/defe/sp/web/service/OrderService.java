package xyz.defe.sp.web.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import xyz.defe.sp.common.entity.spOrder.SpOrder;
import xyz.defe.sp.common.pojo.Cart;
import xyz.defe.sp.common.pojo.ResponseData;
import xyz.defe.sp.common.rest.RestUtil;

@Service
public class OrderService extends BaseService {
    @Autowired
    private ProductService productService;
    @Autowired
    private OrderService orderService;

    public String getOrderToken() {
        ResponseData<String> responseData = request(() -> {
            return RestUtil.INSTANCE.set(rest)
                    .get(baseURL + "orderService/order/token");
        });
        return responseData.getData();
    }

    public SpOrder newOrder(Cart cart) {
        ResponseData<SpOrder> responseData  = request(() -> {
            return RestUtil.INSTANCE.set(rest)
                    .post(baseURL + "orderService/order", cart,
                            new ParameterizedTypeReference<ResponseData<SpOrder>>() {});
        });
        return responseData.getData();
    }

    public SpOrder getOrder(String orderId) {
        ResponseData<SpOrder> responseData = request(() -> {
            return RestUtil.INSTANCE.set(rest)
                    .get(baseURL + "orderService/order/{id}",
                            new ParameterizedTypeReference<ResponseData<SpOrder>>() {}, orderId);
        });
        return responseData.getData();
    }

    public SpOrder getPaidOrder(String orderId) {
        ResponseData<SpOrder> responseData = request(() -> {
            return RestUtil.INSTANCE.set(rest)
                    .get(baseURL + "orderService/order/paid/{id}",
                            new ParameterizedTypeReference<ResponseData<SpOrder>>() {}, orderId);
        });
        return responseData.getData();
    }

}
