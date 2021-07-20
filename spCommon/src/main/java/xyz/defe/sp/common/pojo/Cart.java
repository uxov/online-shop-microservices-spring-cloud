package xyz.defe.sp.common.pojo;

import xyz.defe.sp.common.entity.spProduct.Product;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Cart implements Serializable {
    private String uid;
    private transient String orderToken;

    //{productId : count}
    private Map<String, Integer> counterMap = new HashMap<>();
    private Set<Product> productSet = new HashSet<>();

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getOrderToken() {
        return orderToken;
    }

    public void setOrderToken(String orderToken) {
        this.orderToken = orderToken;
    }

    public Map<String, Integer> getCounterMap() {
        return counterMap;
    }

    public void setCounterMap(Map<String, Integer> counterMap) {
        this.counterMap = counterMap;
    }

    public Set<Product> getProductSet() {
        return productSet;
    }

    public void setProductSet(Set<Product> productSet) {
        this.productSet = productSet;
    }
}
