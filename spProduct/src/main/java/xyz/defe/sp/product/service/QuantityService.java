package xyz.defe.sp.product.service;

import java.util.Map;

public interface QuantityService {

    void checkAndDeduct(String orderId, Map<String, Integer> counterMap) throws InterruptedException;

    void checkAndRestore(String orderId, Map<String, Integer> counterMap) throws InterruptedException;
}
