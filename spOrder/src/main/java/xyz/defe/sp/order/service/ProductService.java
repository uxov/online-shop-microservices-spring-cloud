package xyz.defe.sp.order.service;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import xyz.defe.sp.common.Const;
import xyz.defe.sp.common.entity.spOrder.SpOrder;
import xyz.defe.sp.common.entity.spProduct.Product;
import xyz.defe.sp.common.pojo.Cart;
import xyz.defe.sp.common.pojo.ResponseData;
import xyz.defe.sp.common.rest.RestUtil;

import java.util.*;

@Service
public class ProductService {
    @Autowired
    private Gson gson;
    @Autowired
    private RestTemplate rest;
    @Autowired
    private OrderService orderService;

    @Value("${sp-product-service.url}")
    private String baseURL;

    public List<Product> getProducts(Set<String> idSet) {
        if (idSet.isEmpty()) {return new ArrayList();}
        String ids = String.join(",", idSet);
        ResponseData<List<Product>> responseData = RestUtil.INSTANCE.set(rest)
                .get(baseURL + "/products/{ids}",
                        new ParameterizedTypeReference<ResponseData<List<Product>>>(){}, ids);
        List<Product> list = responseData.getData();
        return list;
    }

    /**
     * when unpaid orders are expired(such as expired time=30 minutes)
     * then request to PRODUCT SERVICE to restore product's quantity
     * if successful then set orders invalid
     */
    public void processExpiredOrders() {
        Long current = new Date().getTime();
        Map<String, Map<String, Integer>> restoreMap = new HashMap<>();
        List<SpOrder> orders = orderService.getUnpaidOrders();
        for (SpOrder order : orders) {
            if (current - order.getCreatedTime().getTime() > Const.EXPIRED_TIME_ORDER_MILLIS) {
                Map<String, Integer> counterMap = ((Cart)gson.fromJson(order.getCartJson(),
                        new TypeToken<Cart>(){}.getType())).getCounterMap();
                restoreMap.put(order.getId(), counterMap);
            }
        }
        ResponseData<Set<String>> responseData = RestUtil.INSTANCE.set(rest)
                .post(baseURL + "/quantity/restore", restoreMap, new ParameterizedTypeReference<ResponseData<Set<String>>>() {});
        Set<String> successfulOrderIdSet = responseData.getData();
        if (successfulOrderIdSet != null && !successfulOrderIdSet.isEmpty()) {
            orders.removeIf(order -> !successfulOrderIdSet.contains(order.getId()));
            orders.forEach(order -> order.setValid(false));
            orderService.saveAll(orders);
        }
    }
}
