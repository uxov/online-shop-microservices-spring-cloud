package xyz.defe.sp.order.dao;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import xyz.defe.sp.common.entity.spOrder.SpOrder;

import java.util.Date;
import java.util.List;

@Repository
public interface OrderDao extends JpaRepository<SpOrder, String> {

    @Query("select o from SpOrder o where o.userId=?1 and o.valid=true ")
    List<SpOrder> getValidOrders(String uid, Pageable page);

    @Query("select o from SpOrder o where o.userId=?1 and o.valid=true and o.paymentState=1")
    List<SpOrder> getUnpaidOrders(String uid);

    @Query("select o from SpOrder o where o.valid=true and o.paymentState=1")
    List<SpOrder> getUnpaidOrders();

    @Query("select o from SpOrder o where id=?1 and o.valid=true and o.paymentState=1")
    SpOrder getToPayOrder(String id);

    SpOrder findByIdAndPaymentState(String id, int paymentState);

    @Query("select o from SpOrder o where id=?1 and o.valid=true and o.paymentState=2")
    SpOrder getPaidOrder(String id);

    @Modifying
    @Transactional
    @Query("update SpOrder o set o.valid=?2 where id=?1 ")
    void setOrderState(String id, boolean state);

    @Modifying
    @Transactional
    @Query("update SpOrder o set o.valid=false,o.invalidReason=?2 where id=?1 ")
    void setOrderInvalid(String id, String invalidReason);

    @Modifying
    @Transactional
    @Query("update SpOrder o set o.paymentState=?2 where id=?1 ")
    void setOrderPaymentState(String id, int state);
}
