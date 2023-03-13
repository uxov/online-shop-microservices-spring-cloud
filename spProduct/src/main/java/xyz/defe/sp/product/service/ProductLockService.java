package xyz.defe.sp.product.service;

import org.redisson.RedissonMultiLock;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import xyz.defe.sp.common.Const;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
public class ProductLockService {
    @Autowired
    public RedissonClient redisson;

    /**
     * to prevent requests keep retrying to get multi lock,
     * so get the global lock first before get multi lock,
     * after get multi lock then unlock global lock immediately.
     * @param productIdSet
     * @return
     */
    RedissonMultiLock getProductMultiLock(Set<String> productIdSet) {
        List<RLock> locks = new ArrayList<>();
        productIdSet.forEach(productId -> {
            RLock lock = redisson.getLock(Const.LOCK_KEY_PRODUCT_PREFIX + productId);
            locks.add(lock);
        });
        RedissonMultiLock mlock = new RedissonMultiLock(locks.toArray(new RLock[locks.size()]));
        return mlock;
    }
}
