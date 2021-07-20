package xyz.defe.sp.product.service;

import xyz.defe.sp.common.entity.spProduct.Product;
import xyz.defe.sp.common.pojo.PageQuery;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface ProductService {
    void addProducts(List<Product> products);

    List<Product> getProducts(PageQuery pageQuery);

    Product getById(String id);

    List<Product> getByIds(String ids);

    /**
     *
     * @param ids
     * @return {productId : quantity}
     */
    Map<String, Integer> getQuantity(String ids);

    /**
     * @param counterMap orderId
     * @param counterMap {productId : count}
     * @return
     */
    int deductQuantity(String orderId, Map<String, Integer> counterMap) throws Exception;

    int checkAndDeduct(String orderId, Map<String, Integer> counterMap) throws Exception;

    Set<String> restoreProductQuantity(Map<String, Map<String, Integer>> restoreMap);
}
