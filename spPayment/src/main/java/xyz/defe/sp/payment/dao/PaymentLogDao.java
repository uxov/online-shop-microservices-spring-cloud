package xyz.defe.sp.payment.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import xyz.defe.sp.common.entity.spPayment.PaymentLog;

@Repository
public interface PaymentLogDao extends JpaRepository<PaymentLog, String> {
    PaymentLog findByOrderId(String orderId);
}
