package xyz.defe.sp.order.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import xyz.defe.sp.common.Cache;
import xyz.defe.sp.common.Const;
import xyz.defe.sp.common.pojo.Cart;
import xyz.defe.sp.common.response.ResponseDataResult;
import xyz.defe.sp.order.service.OrderService;

@RestController
@ResponseDataResult
public class OrderController {
    @Autowired
    private OrderService orderService;
    @Autowired
    public Cache cache;

    /**
     * prevent duplicate submissions
     * client submit order with a token
     * server verify before create order
     * @return
     */
    @GetMapping("order/token")
    public Object getOrderToken() {
        return orderService.getOrderToken();
    }

    @PostMapping("order")
    public Object newOrder(@RequestBody Cart cart) {
        try {
            return orderService.newOrder(cart);
        } catch (Throwable e) {
            cache.delete(Const.TOKEN_ORDER_PREFIX + cart.getOrderToken());
            throw e;
        }
    }

    @GetMapping("order/{id}")
    public Object getOrder(@PathVariable String id) {
        return orderService.getOrder(id);
    }

    @GetMapping("order/unpaid")
    public Object getUnpaidOrders(String uid) {
        return orderService.getUnpaidOrders(uid);
    }

    @GetMapping("order/paid/{id}")
    public Object getPaidOrder(@PathVariable String id) {
        return orderService.getPaidOrder(id);
    }
}
