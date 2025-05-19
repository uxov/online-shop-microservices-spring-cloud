package xyz.defe.sp.payment;

import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import xyz.defe.sp.payment.service.SendMsgToMQ;

import java.util.concurrent.TimeUnit;

@Component
public class Scheduler {
    @Autowired
    private SendMsgToMQ sendMsgToMQ;
    @Autowired
    public RedissonClient redisson;
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Scheduled(fixedDelay = 15000)
    public void checkAndSendMessage() {
        RLock lock = redisson.getLock("scheduledTask-spPayment-checkAndSendMessage");
        try {
            if (!lock.tryLock(3, 30, TimeUnit.SECONDS)) {return;}
            sendMsgToMQ.resendOrderMsg();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            lock.unlock();
        }
    }
}
