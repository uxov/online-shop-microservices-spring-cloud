package xyz.defe.sp.web.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import xyz.defe.sp.common.entity.spOrder.SpOrder;
import xyz.defe.sp.common.pojo.Cart;
import xyz.defe.sp.web.api.OrderFeignClient;

@Service
public class OrderService {
    @Autowired
    private OrderFeignClient orderFeignClient;

    public String getOrderToken() {
        return orderFeignClient.getOrderToken().getData();
    }

    public SpOrder newOrder(Cart cart) {
        return orderFeignClient.newOrder(cart).getData();
    }

    public SpOrder getOrder(String orderId) {
        return orderFeignClient.getOrder(orderId).getData();
    }

    public SpOrder getPaidOrder(String orderId) {
        return orderFeignClient.getPaidOrder(orderId).getData();
    }

}
