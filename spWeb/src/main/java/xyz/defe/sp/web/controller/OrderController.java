package xyz.defe.sp.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import xyz.defe.sp.common.pojo.Cart;
import xyz.defe.sp.common.pojo.ResponseData;
import xyz.defe.sp.web.service.OrderService;

@RestController
@RequestMapping("order")
public class OrderController extends BaseController {
    @Autowired
    private OrderService orderService;

    @GetMapping("orderToken")
    public ResponseData getOrderToken() {
        return response(() -> orderService.getOrderToken());
    }

    @PostMapping("/")
    public ResponseData newOrder(@RequestBody Cart cart) {
        return response(() -> orderService.newOrder(cart));
    }

    @GetMapping("{orderId}")
    public ResponseData getOrder(@PathVariable String orderId) {
        return response(() -> orderService.getOrder(orderId));
    }

    @GetMapping("paid/{id}")
    public ResponseData getPaidOrder(@PathVariable String id) {
        return response(() -> orderService.getPaidOrder(id));
    }
}
