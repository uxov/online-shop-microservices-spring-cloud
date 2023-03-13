package xyz.defe.sp.payment.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import xyz.defe.sp.common.pojo.OrderMsg;
import xyz.defe.sp.payment.config.AmqpConfig;

import java.util.concurrent.CompletableFuture;

@Service
public class MqMessageService {
    @Autowired
    private RabbitTemplate rabbitTemplate;
    final Logger log = LoggerFactory.getLogger(this.getClass());

    public void send(String localMessageId, OrderMsg message) {
        CompletableFuture.runAsync(() -> {
            rabbitTemplate.convertAndSend(AmqpConfig.EXCHANGE_NAME,
                    AmqpConfig.ROUTING_KEY_SET_ORDER_PAID, message,
                    new CorrelationData(localMessageId));
            log.info("send message to ORDER SERVICE - to set order paid");
        });
    }

}
