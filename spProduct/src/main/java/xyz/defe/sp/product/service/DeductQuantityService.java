package xyz.defe.sp.product.service;

import com.google.gson.Gson;
import org.redisson.RedissonMultiLock;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import xyz.defe.sp.common.Const;
import xyz.defe.sp.common.entity.spProduct.Product;
import xyz.defe.sp.common.exception.ExceptionUtil;
import xyz.defe.sp.common.util.CheckParam;
import xyz.defe.sp.product.dao.DeductQuantityLogDao;
import xyz.defe.sp.product.dao.ProductDao;
import xyz.defe.sp.product.entity.DeductQuantityLog;

import javax.transaction.Transactional;
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
    public RedissonClient redisson;
    @Autowired
    private ProductLockService productLockService;
    @Autowired
    private DeductQuantityLogDao deductQuantityLogDao;
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Transactional
    public void checkAndDeduct(String orderId, Map<String, Integer> counterMap) {
        CheckParam.check(orderId, "orderId is null or empty");
        CheckParam.check(counterMap, "counterMap is null or empty");

        //to prevent duplicate requests
        RLock lock = redisson.getLock(Const.LK_ORDER_DEDUCT_QUANTITY_PRE + orderId);
        if (!lock.tryLock()) {ExceptionUtil.warn("tryLock() failed in checkAndDeduct()");}
        try {
            DeductQuantityLog dqlog = deductQuantityLogDao.findByOrderId(orderId);
            if (dqlog != null) {
                log.info("Duplicate submission: the order has been processed(deduct product quantity),order id={}", orderId);
                return;
            }
            deductQuantity(orderId, counterMap);
        } finally {
            lock.unlock();
        }
    }

    @Transactional
    public void deductQuantity(String orderId, Map<String, Integer> counterMap) {
        RedissonMultiLock mlock = productLockService.getProductMultiLock(counterMap.keySet());
        mlock.lock();
        try {
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
        } finally {
            mlock.unlock();
        }
    }

}

