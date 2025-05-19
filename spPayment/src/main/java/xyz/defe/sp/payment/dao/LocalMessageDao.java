package xyz.defe.sp.payment.dao;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import xyz.defe.sp.common.entity.general.LocalMessage;
import xyz.defe.sp.common.enums.LocalMsgState;

import java.util.List;

@Repository
public interface LocalMessageDao extends JpaRepository<LocalMessage, String> {

    @Query("select m from LocalMessage m where m.msgType='OrderMsg' and m.msgState='PENDING_RESEND'")
    List<LocalMessage> getResendOrderMsgs();

    @Modifying
    @Transactional
    @Query("update LocalMessage set msgState=?1 ,updatedTime=CURRENT_TIMESTAMP where id=?2")
    void setMsgState(LocalMsgState megState, String messageId);
}
