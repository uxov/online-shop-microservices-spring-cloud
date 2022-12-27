package xyz.defe.sp.product.service;

import org.redisson.RedissonMultiLock;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import xyz.defe.sp.common.Const;
import xyz.defe.sp.common.entity.spProduct.Product;
import xyz.defe.sp.common.util.CheckParam;
import xyz.defe.sp.product.dao.DeductQuantityLogDao;
import xyz.defe.sp.product.dao.ProductDao;
import xyz.defe.sp.product.entity.DeductQuantityLog;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Map;

@Service
public class RestoreQuantityService {
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
    public void checkAndRestore(String orderId, Map<String, Integer> counterMap) {
        CheckParam.check(orderId, "orderId is null or empty");
        CheckParam.check(counterMap, "counterMap is null or empty");

        //to prevent duplicate requests
        RLock lock = redisson.getLock(Const.LK_ORDER_DEDUCT_QUANTITY_PRE + orderId);
        if (!lock.tryLock()) {return;}
        try {
            DeductQuantityLog record = deductQuantityLogDao.findByOrderIdAndState(orderId, 1);
            if (log == null) {return;}
            restoreQuantity(orderId, counterMap, record);
        } finally {
            lock.unlock();
        }
    }

    /**
     * @param orderId
     * @param counterMap {productId : counter}
     * @return
     */
    @Transactional
    public void restoreQuantity(String orderId, Map<String, Integer> counterMap, DeductQuantityLog record) {
        RedissonMultiLock mlock = productLockService.getProductMultiLock(counterMap.keySet());
        mlock.lock();
        try {
            List<Product> products = productDao.findAllById(counterMap.keySet());
            products.forEach(product -> {
                product.setQuantity(product.getQuantity() + counterMap.get(product.getId()));
            });
            productDao.saveAll(products);
            record.setState(0);
            deductQuantityLogDao.saveAndFlush(record);

            log.info("restore product quantity successful, order id={}", orderId);
        } finally {
            mlock.unlock();
        }
    }
}
