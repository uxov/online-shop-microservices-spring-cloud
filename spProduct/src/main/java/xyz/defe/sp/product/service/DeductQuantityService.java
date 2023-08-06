package xyz.defe.sp.product.service;

import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import xyz.defe.sp.common.entity.spProduct.Product;
import xyz.defe.sp.common.exception.ExceptionUtil;
import xyz.defe.sp.product.dao.DeductQuantityLogDao;
import xyz.defe.sp.product.dao.ProductDao;
import xyz.defe.sp.product.entity.DeductQuantityLog;

import jakarta.transaction.Transactional;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class DeductQuantityService {
    @Autowired
    private Gson gson;
    @Autowired
    private ProductDao productDao;
    @Autowired
    private DeductQuantityLogDao deductQuantityLogDao;
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Transactional
    public void deductQuantity(String orderId, Map<String, Integer> counterMap) {
        int remain = 0;
        Set<String> productIdSet = counterMap.keySet();
        List<Product> products = productDao.findAllById(productIdSet);

        //check product quantity
        for (Product product : products) {
            remain = product.getQuantity() - counterMap.get(product.getId());
            if (remain < 0) {
                ExceptionUtil.warn("product is out of stock, product id=" + product.getId());
            }
            product.setQuantity(remain);
        }

        //update quantity
        productDao.saveAll(products);

        Map dataMap = new HashMap<>();
        dataMap.put("products", products);
        dataMap.put("counterMap", counterMap);
        DeductQuantityLog record = new DeductQuantityLog();
        record.setOrderId(orderId);
        record.setDataJson(gson.toJson(dataMap));
        deductQuantityLogDao.saveAndFlush(record);

        log.info("deduct quantity successful,order id={}", orderId);
    }

}

