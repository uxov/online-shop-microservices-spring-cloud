package xyz.defe.sp.product.service;

import com.google.common.base.Strings;
import com.google.common.collect.Sets;
import com.google.gson.Gson;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import xyz.defe.sp.common.Const;
import xyz.defe.sp.common.WarnException;
import xyz.defe.sp.common.entity.spProduct.Product;
import xyz.defe.sp.common.pojo.PageQuery;
import xyz.defe.sp.product.dao.ProductDao;
import xyz.defe.sp.product.dao.DeductQuantityLogDao;
import xyz.defe.sp.product.entity.DeductQuantityLog;

import javax.transaction.Transactional;
import java.util.*;

@Service
public class ProductServiceImpl implements ProductService {
    @Autowired
    private Gson gson;
    @Autowired
    private ProductDao productDao;
    @Autowired
    public RedissonClient redisson;
    @Autowired
    private DeductQuantityLogDao deductQuantityLogDao;
    @Autowired
    private RestoreQuantityService restoreQuantityService;
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
        if (Strings.isNullOrEmpty(ids)) {return new ArrayList<>();}
        Set idSet = Sets.newHashSet(ids.split(","));
        return productDao.findAllById(idSet);
    }

    @Override
    public Map<String, Integer> getQuantity(String ids) {
        Map<String, Integer> result = new HashMap<>();
        if (!Strings.isNullOrEmpty(ids)) {
            String[] idArr = ids.split(",");
            Set<String> idSet = new HashSet<>(Arrays.asList(idArr));
            List<Product> data = productDao.findAllById(idSet);
            data.forEach(e -> result.put(e.getId(), e.getQuantity()));
        }
        return result;
    }

    private void checkBeforeDeductQuantity(String orderId, Map<String, Integer> counterMap) throws WarnException {
        if (counterMap == null || counterMap.isEmpty()) {
            throw new WarnException("counterMap is empty");
        }
        if (Strings.isNullOrEmpty(orderId)) {
            throw new WarnException("orderId is empty");
        }
    }

    //another way: load quantities to cache,split product's quantity and use segment locks
    @Override
    @Transactional
    public int deductQuantity(String orderId, Map<String, Integer> counterMap) throws Exception {
        checkBeforeDeductQuantity(orderId, counterMap);
        DeductQuantityLog log = deductQuantityLogDao.findByOrderId(orderId);
        if (log != null) {
            this.log.warn("the order has been processed(deduct product quantity),order id={}", orderId);
            return 1;
        }

        RLock lock = redisson.getLock(Const.LOCK_KEY_PRODUCTS);
        try {
            lock.lock();

            int remain = 0;
            Set<String> productIdSet = counterMap.keySet();
            List<Product> products = productDao.findAllById(productIdSet);

            //check product quantity
            for (Product product : products) {
                remain = product.getQuantity() - counterMap.get(product.getId());
                if (remain < 0) {
                    throw new WarnException("product is out of stock, product id=" + product.getId());
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
        } finally {
            lock.unlock();
        }
        this.log.info("deduct quantity successful,order id={}", orderId);
        return 1;
    }

    /**
     * @param orderId
     * @param counterMap
     */
    @Override
    @Transactional
    public int checkAndDeduct(String orderId, Map<String, Integer> counterMap) throws Exception {
        return deductQuantity(orderId, counterMap);
    }

    @Override
    public Set<String> restoreProductQuantity(Map<String, Map<String, Integer>> restoreMap) {
        boolean done = false;
        Set<String> successfulOrderIdSet = new HashSet<>();
        if (restoreMap != null && !restoreMap.isEmpty()) {
            for (Map.Entry<String, Map<String, Integer>> entity : restoreMap.entrySet()) {
                try {
                    done = restoreQuantityService.restoreQuantity(entity.getKey(), entity.getValue());
                    if (done) {successfulOrderIdSet.add(entity.getKey());}
                } catch (Exception e) {
                    log.error(e.getMessage());
                    e.printStackTrace();
                }
            }
        }
        return successfulOrderIdSet;
    }

}
