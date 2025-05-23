package xyz.defe.sp.payment.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import xyz.defe.sp.common.entity.spProduct.Product;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
public class ProductService {
    @Autowired
    private ProductFeignClient productFeignClient;

    public List<Product> getProducts(Set<String> idSet) {
        if (idSet.isEmpty()) {return new ArrayList();}
        String ids = String.join(",", idSet);
        List<Product> list = productFeignClient.getByIds(ids).getData();
        return list;
    }
}
