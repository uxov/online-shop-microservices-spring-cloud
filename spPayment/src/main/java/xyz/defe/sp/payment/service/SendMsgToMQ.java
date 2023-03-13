package xyz.defe.sp.payment.service;

import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import xyz.defe.sp.common.entity.general.LocalMessage;
import xyz.defe.sp.common.pojo.OrderMsg;

import java.util.List;

@Component
public class SendMsgToMQ {
    @Autowired
    private Gson gson;
    @Autowired
    private LocalMessageService localMessageService;
    @Autowired
    private MqMessageService mqMessageService;
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    public void resendOrderMsg() {
        List<LocalMessage> list = localMessageService.getRetryOrderMsgs();
        String msgJson = "";
        for (LocalMessage m : list) {
            try {
                msgJson = m.getMsgJson();
                OrderMsg msg = gson.fromJson(msgJson, OrderMsg.class);
                localMessageService.setRetry(m.getId(), 0); //before send()
                mqMessageService.send(m.getId(), msg);
            } catch (Exception e) {
                log.error("send message failed,OrderMsg id={}", m.getId());
                e.printStackTrace();
            }
        }
    }
}
