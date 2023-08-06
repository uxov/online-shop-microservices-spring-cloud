package xyz.defe.sp.payment.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import xyz.defe.sp.common.entity.general.LocalMessage;

import jakarta.transaction.Transactional;
import java.util.List;

@Repository
public interface LocalMessageDao extends JpaRepository<LocalMessage, String> {

    @Query("select m from LocalMessage m where m.msgType='OrderMsg' and m.sendState=1 and m.retry=1")
    List<LocalMessage> getRetryOrderMsgs();

    @Modifying
    @Transactional
    @Query("update LocalMessage set sendState=?1 where id=?2")
    void setSendState(Integer sendState, String messageId);

    @Modifying
    @Transactional
    @Query("update LocalMessage set retry=?1 where id=?2")
    void setRetry(Integer time, String messageId);
}
