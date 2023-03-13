package xyz.defe.sp.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import xyz.defe.sp.common.entity.spOrder.SpOrder;
import xyz.defe.sp.common.pojo.Cart;
import xyz.defe.sp.common.pojo.ResponseData;
import xyz.defe.sp.common.response.ResponseDataResult;
import xyz.defe.sp.web.service.OrderService;

import java.util.concurrent.ExecutionException;

@RestController
@ResponseDataResult
@RequestMapping("order")
public class OrderController {
    @Autowired
    private OrderService orderService;

    @GetMapping("orderToken")
    public ResponseData<String> getOrderToken() {
        return orderService.getOrderToken();
    }

    @PostMapping
    public ResponseData<SpOrder> newOrder(@RequestBody Cart cart) {
        return orderService.newOrder(cart);
    }

    @GetMapping("{orderId}")
    public ResponseData<SpOrder> getOrder(@PathVariable String orderId) {
        return orderService.getOrder(orderId);
    }

    @GetMapping("paid/{id}")
    public ResponseData<SpOrder> getPaidOrder(@PathVariable String id) throws ExecutionException, InterruptedException {
        return orderService.getPaidOrder(id).get();
    }
}
