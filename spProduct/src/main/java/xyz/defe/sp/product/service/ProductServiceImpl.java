package xyz.defe.sp.product.service;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import xyz.defe.sp.common.entity.spProduct.Product;
import xyz.defe.sp.common.pojo.PageQuery;
import xyz.defe.sp.product.dao.ProductDao;

import java.util.*;

@Service
public class ProductServiceImpl implements ProductService {
    @Autowired
    private ProductDao productDao;
    @Autowired
    private QuantityService quantityService;
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Override
    public void addProducts(List<Product> products) {
        productDao.saveAll(products);
    }

    @Override
    public List<Product> getProducts(PageQuery pageQuery) {
        return productDao.findAll(pageQuery.getPageRequest()).getContent();
    }

    @Override
    public Product getById(String id) {
        Optional<Product> product = productDao.findById(id);
        if (product.isPresent()) {return product.get();}
        return null;
    }

    @Override
    public List<Product> getByIds(String ids) {
        if (StringUtils.isBlank(ids)) {return new ArrayList<>();}
        Set<String> idSet = Set.of(ids.split(","));
        return productDao.findAllById(idSet);
    }

    @Override
    public Map<String, Integer> getQuantity(String ids) {
        Map<String, Integer> result = new HashMap<>();
        if (StringUtils.isNotBlank(ids)) {
            Set<String> idSet = Set.of(ids.split(","));
            List<Product> data = productDao.findAllById(idSet);
            data.forEach(e -> result.put(e.getId(), e.getQuantity()));
        }
        return result;
    }

    /**
     * @param restoreMap {orderId : {productId : count}}
     * @return
     */
    @Override
    public Set<String> restoreProductQuantity(Map<String, Map<String, Integer>> restoreMap) {
        Set<String> successfulOrderIdSet = new HashSet<>();
        if (restoreMap != null && !restoreMap.isEmpty()) {
            restoreMap.entrySet().forEach(entity -> {
                try {
                    quantityService.checkAndRestore(entity.getKey(), entity.getValue());
                    successfulOrderIdSet.add(entity.getKey());
                } catch (Exception e) {
                    log.error(e.getMessage());
                    e.printStackTrace();
                }
            });
        }
        return successfulOrderIdSet;
    }

}
