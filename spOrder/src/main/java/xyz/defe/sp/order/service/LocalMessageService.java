package xyz.defe.sp.order.service;

import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import xyz.defe.sp.common.entity.general.LocalMessage;
import xyz.defe.sp.common.enums.LocalMsgState;
import xyz.defe.sp.common.pojo.OrderMsg;
import xyz.defe.sp.order.dao.LocalMessageDao;

import java.util.List;

@Service
public class LocalMessageService {
    @Autowired
    private Gson gson;
    @Autowired
    private LocalMessageDao localMessageDao;
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    public List<LocalMessage> getResendOrderMsgs() {
        return localMessageDao.getResendOrderMsgs();
    }

    public void saveOrderMessage(OrderMsg message) {
        LocalMessage localMessage = new LocalMessage();
        localMessage.setId(message.getId());
        localMessage.setMsgType("OrderMsg");
        localMessage.setMsgJson(gson.toJson(message));
        localMessageDao.save(localMessage);
    }

    public void setMessageState(String messageId, LocalMsgState state) {
        localMessageDao.setMsgState(state, messageId);
    }
}
