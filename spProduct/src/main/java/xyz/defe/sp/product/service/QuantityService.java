package xyz.defe.sp.product.service;

import org.redisson.RedissonMultiLock;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import xyz.defe.sp.common.Const;
import xyz.defe.sp.common.exception.ExceptionUtil;
import xyz.defe.sp.common.util.CheckParam;
import xyz.defe.sp.product.dao.DeductQuantityLogDao;
import xyz.defe.sp.product.dao.ProductDao;
import xyz.defe.sp.product.entity.DeductQuantityLog;

import java.util.Map;

@Service
public class QuantityService {
    @Autowired
    private ProductDao productDao;
    @Autowired
    public RedissonClient redisson;
    @Autowired
    private ProductLockService productLockService;
    @Autowired
    private DeductQuantityLogDao deductQuantityLogDao;
    @Autowired
    private DeductQuantityService deductQuantityService;
    @Autowired
    private RestoreQuantityService restoreQuantityService;
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    public void checkAndDeduct(String orderId, Map<String, Integer> counterMap) {
        CheckParam.check(orderId, "orderId is null or empty");
        CheckParam.check(counterMap, "counterMap is null or empty");

        //to prevent duplicate requests
        RLock lock = redisson.getLock(Const.LK_ORDER_DEDUCT_QUANTITY_PRE + orderId);
        if (!lock.tryLock()) {
            ExceptionUtil.warn("tryLock() failed in checkAndDeduct()");}
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

    private void deductQuantity(String orderId, Map<String, Integer> counterMap) {
        RedissonMultiLock mlock = productLockService.getProductMultiLock(counterMap.keySet());
        mlock.lock();
        try {
            deductQuantityService.deductQuantity(orderId,counterMap);
        } finally {
            mlock.unlock();
        }
    }

    public void checkAndRestore(String orderId, Map<String, Integer> counterMap) {
        CheckParam.check(orderId, "orderId is null or empty");
        CheckParam.check(counterMap, "counterMap is null or empty");

        //to prevent duplicate requests
        RLock lock = redisson.getLock(Const.LK_ORDER_RESTORE_QUANTITY_PRE + orderId);
        if (!lock.tryLock()) {return;}
        try {
            DeductQuantityLog record = deductQuantityLogDao.findByOrderIdAndState(orderId, 1);
            if (record == null) {return;}
            restoreQuantity(orderId, counterMap, record);
        } finally {
            lock.unlock();
        }
    }

    private void restoreQuantity(String orderId, Map<String, Integer> counterMap, DeductQuantityLog record) {
        RedissonMultiLock mlock = productLockService.getProductMultiLock(counterMap.keySet());
        mlock.lock();
        try {
            restoreQuantityService.restoreQuantity(orderId, counterMap, record);
        } finally {
            mlock.unlock();
        }
    }

}
