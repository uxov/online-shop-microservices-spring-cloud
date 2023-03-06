package xyz.defe.sp.order.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import xyz.defe.sp.common.Cache;
import xyz.defe.sp.common.Const;
import xyz.defe.sp.common.entity.spOrder.SpOrder;
import xyz.defe.sp.common.pojo.Cart;
import xyz.defe.sp.common.response.ResponseDataResult;
import xyz.defe.sp.order.service.OrderService;

import java.util.List;

@RestController
@ResponseDataResult
public class OrderController {
    @Autowired
    private OrderService orderService;
    @Autowired
    public Cache cache;

    /**
     * to prevent duplicate submissions
     * client submit order with a token
     * server verify before create order
     * @return
     */
    @GetMapping("order/token")
    public String getOrderToken() {
        return orderService.getOrderToken();
    }

    @PostMapping("order")
    public SpOrder newOrder(@RequestBody Cart cart) {
        try {
            return orderService.newOrder(cart);
        } catch (Throwable e) {
            cache.delete(Const.TOKEN_ORDER_PREFIX + cart.getOrderToken());
            throw e;
        }
    }

    @GetMapping("order/{id}")
    public SpOrder getOrder(@PathVariable String id) {
        return orderService.getOrder(id);
    }

    @GetMapping("order/unpaid")
    public List<SpOrder> getUnpaidOrders(String uid) {
        return orderService.getUnpaidOrders(uid);
    }

    @GetMapping("order/paid/{id}")
    public SpOrder getPaidOrder(@PathVariable String id) {
        return orderService.getPaidOrder(id);
    }

    @GetMapping("order/toPay/{id}")
    public SpOrder getToPayOrder(@PathVariable String id) {
        return orderService.getToPayOrder(id);
    }
}
