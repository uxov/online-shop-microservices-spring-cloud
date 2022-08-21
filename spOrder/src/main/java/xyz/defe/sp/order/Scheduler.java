package xyz.defe.sp.order;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import xyz.defe.sp.order.service.OrderService;
import xyz.defe.sp.order.service.SendMsgToMQ;

@Component
public class Scheduler {
    @Autowired
    private SendMsgToMQ sendMsgToMQ;
    @Autowired
    private OrderService orderService;
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Scheduled(fixedRate = 3000) //millis
    public void checkAndSendMessage() {
        try {
            sendMsgToMQ.resendOrderMsg();
        } catch (Exception e) {
            log.error(e.getMessage());
            e.printStackTrace();
        }
    }

    @Scheduled(fixedRate = 300000)
    public void checkExpiredOrders() {
        try {
            orderService.processExpiredOrders();
        } catch (Exception e) {
            log.error(e.getMessage());
            e.printStackTrace();
        }
    }
}
