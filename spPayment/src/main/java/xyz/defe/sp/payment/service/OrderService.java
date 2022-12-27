package xyz.defe.sp.payment.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import xyz.defe.sp.common.entity.spOrder.SpOrder;

@Service
public class OrderService {
    @Autowired
    private OrderFeignClient orderFeignClient;
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    public SpOrder getOrder(String id) {
        return orderFeignClient.getOrder(id).getData();
    }

    public SpOrder getToPayOrder(String orderId) {
        return orderFeignClient.getToPayOrder(orderId).getData();
    }
}
