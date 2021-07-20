package xyz.defe.sp.product.service;

import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import xyz.defe.sp.common.Const;
import xyz.defe.sp.common.entity.spProduct.Product;
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
    private DeductQuantityLogDao deductQuantityLogDao;
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Transactional
    public boolean restoreQuantity(String orderId, Map<String, Integer> counterMap) {
        boolean done = false;
        if (counterMap == null || counterMap.keySet().isEmpty()) {return done;}
        DeductQuantityLog record = deductQuantityLogDao.findByOrderIdAndState(orderId, 1);
        if (record == null) {return done;}
        RLock lock = redisson.getLock(Const.LOCK_KEY_PRODUCTS);
        try {
            lock.lock();
            List<Product> products = productDao.findAllById(counterMap.keySet());
            products.forEach(product -> {
                product.setQuantity(product.getQuantity() + counterMap.get(product.getId()));
            });
            productDao.saveAll(products);
            record.setState(0);
            deductQuantityLogDao.saveAndFlush(record);
            done = true;
        } finally {
            lock.unlock();
        }
        log.info("restore product quantity successful, order id={}", orderId);
        return done;
    }
}
