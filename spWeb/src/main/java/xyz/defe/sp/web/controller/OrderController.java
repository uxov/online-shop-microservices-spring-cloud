package xyz.defe.sp.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import xyz.defe.sp.common.pojo.Cart;
import xyz.defe.sp.common.response.ResponseDataResult;
import xyz.defe.sp.web.service.OrderService;

@RestController
@ResponseDataResult
@RequestMapping("order")
public class OrderController {
    @Autowired
    private OrderService orderService;

    @GetMapping("orderToken")
    public Object getOrderToken() {
        return orderService.getOrderToken();
    }

    @PostMapping
    public Object newOrder(@RequestBody Cart cart) {
        return orderService.newOrder(cart);
    }

    @GetMapping("{orderId}")
    public Object getOrder(@PathVariable String orderId) {
        return orderService.getOrder(orderId);
    }

    @GetMapping("paid/{id}")
    public Object getPaidOrder(@PathVariable String id) {
        return orderService.getPaidOrder(id);
    }
}
