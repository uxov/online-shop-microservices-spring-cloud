package xyz.defe.sp.order.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import xyz.defe.sp.common.Cache;
import xyz.defe.sp.common.Const;
import xyz.defe.sp.common.ResponseWrap;
import xyz.defe.sp.common.pojo.Cart;
import xyz.defe.sp.common.pojo.ResponseData;
import xyz.defe.sp.order.service.OrderService;

@RestController
public class OrderController {
    @Autowired
    private ResponseWrap response;
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
    public ResponseData getOrderToken() {
        return response.wrap(() -> orderService.getOrderToken());
    }

    @PostMapping("order")
    public ResponseData newOrder(@RequestBody Cart cart) throws Exception {
        return response.wrap(() -> {
            try {
                return orderService.newOrder(cart);
            } catch (Throwable e) {
                cache.delete(Const.TOKEN_ORDER_PREFIX + cart.getOrderToken());
                throw e;
            }
        });
    }

    @GetMapping("order/{id}")
    public ResponseData getOrder(@PathVariable String id) {
        return response.wrap(() -> orderService.getOrder(id));
    }

    @GetMapping("order/unpaid")
    public ResponseData getUnpaidOrders(String uid) {
        return response.wrap(() -> orderService.getUnpaidOrders(uid));
    }

    @GetMapping("order/paid/{id}")
    public ResponseData getPaidOrder(@PathVariable String id) {
        return response.wrap(() -> orderService.getPaidOrder(id));
    }
}
