package xyz.defe.sp.web.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import xyz.defe.sp.common.entity.spOrder.SpOrder;
import xyz.defe.sp.common.pojo.Cart;
import xyz.defe.sp.common.pojo.ResponseData;
import xyz.defe.sp.web.api.OrderFeignClient;

@Service
public class OrderService {
    @Autowired
    private OrderFeignClient orderFeignClient;

    public ResponseData<String> getOrderToken() {
        return orderFeignClient.getOrderToken();
    }

    public ResponseData<SpOrder> newOrder(Cart cart) {
        return orderFeignClient.newOrder(cart);
    }

    public ResponseData<SpOrder> getOrder(String orderId) {
        return orderFeignClient.getOrder(orderId);
    }

    public ResponseData<SpOrder> getPaidOrder(String orderId) {
        return orderFeignClient.getPaidOrder(orderId);
    }

}
