package xyz.defe.sp.test.feignClient.spWeb;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import xyz.defe.sp.common.entity.spOrder.SpOrder;
import xyz.defe.sp.common.entity.spPayment.PaymentLog;
import xyz.defe.sp.common.entity.spProduct.Product;
import xyz.defe.sp.common.pojo.Cart;
import xyz.defe.sp.common.pojo.ResponseData;

import java.util.List;
import java.util.Map;

//request to spWeb
@FeignClient(name = "webClient", url = "http://localhost:9090/")
public interface SpWeb {
    @GetMapping("product/list")
    ResponseData<List<Product>> getProducts(@RequestParam int pageNum, @RequestParam int pageSize);

    @PostMapping("login")
    ResponseData<Map<String, String>> login(@RequestParam String uname, @RequestParam String pwd);

    @GetMapping("order/orderToken")
    ResponseData<String> getOrderToken();

    @PostMapping("order")
    ResponseData<SpOrder> newOrder(@RequestBody Cart cart);

    @PostMapping("payment/pay")
    ResponseData<PaymentLog> pay(@RequestParam String orderId);

    @GetMapping("order/paid/{id}")
    ResponseData<SpOrder> getPaidOrder(@PathVariable String id);
}
