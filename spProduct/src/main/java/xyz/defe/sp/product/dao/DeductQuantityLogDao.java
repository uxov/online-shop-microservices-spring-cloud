package xyz.defe.sp.product.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import xyz.defe.sp.product.entity.DeductQuantityLog;

@Repository
public interface DeductQuantityLogDao extends JpaRepository<DeductQuantityLog, String> {
    DeductQuantityLog findByOrderId(String orderId);

    DeductQuantityLog findByOrderIdAndState(String orderId, Integer state);
}
