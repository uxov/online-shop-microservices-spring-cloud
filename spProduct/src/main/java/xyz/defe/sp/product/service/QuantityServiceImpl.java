package xyz.defe.sp.product.service;

import org.redisson.RedissonMultiLock;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import xyz.defe.sp.common.exception.ExceptionUtil;
import xyz.defe.sp.common.util.CheckParam;
import xyz.defe.sp.product.dao.DeductQuantityLogDao;
import xyz.defe.sp.product.dao.ProductDao;
import xyz.defe.sp.product.entity.DeductQuantityLog;

import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public class QuantityServiceImpl implements QuantityService {
    @Autowired
    private ProductDao productDao;
    @Autowired
    private RedissonClient redisson;
    @Autowired
    private ProductLockService productLockService;
    @Autowired
    private DeductQuantityLogDao deductQuantityLogDao;
    @Autowired
    private DeductQuantityService deductQuantityService;
    @Autowired
    private RestoreQuantityService restoreQuantityService;
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Override
    public void checkAndDeduct(String orderId, Map<String, Integer> counterMap) throws InterruptedException {
        CheckParam.check(orderId, "orderId is null or empty");
        CheckParam.check(counterMap, "counterMap is null or empty");

        RedissonMultiLock lock = productLockService.getProductMultiLock(counterMap.keySet());
        try {
            if (!lock.tryLock(5, 30,TimeUnit.SECONDS)) {
                ExceptionUtil.warn("tryLock() failed in checkAndDeduct()");
            }
            DeductQuantityLog dqlog = deductQuantityLogDao.findByOrderId(orderId);
            if (dqlog != null) {
                log.info("Duplicate submission: the order has been processed(deduct product quantity),order id={}", orderId);
                return;
            }
            deductQuantityService.deductQuantity(orderId,counterMap);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void checkAndRestore(String orderId, Map<String, Integer> counterMap) throws InterruptedException {
        CheckParam.check(orderId, "orderId is null or empty");
        CheckParam.check(counterMap, "counterMap is null or empty");

        RedissonMultiLock lock = productLockService.getProductMultiLock(counterMap.keySet());
        try {
            if (!lock.tryLock(5, 30,TimeUnit.SECONDS)) {
                ExceptionUtil.warn("tryLock() failed in checkAndDeduct()");
            }
            DeductQuantityLog record = deductQuantityLogDao.findByOrderIdAndState(orderId, 1);
            if (record == null) {return;}
            restoreQuantityService.restoreQuantity(orderId, counterMap, record);
        } finally {
            lock.unlock();
        }
    }

}
