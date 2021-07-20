package xyz.defe.sp.payment.service;

import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import xyz.defe.sp.common.pojo.OrderMsg;
import xyz.defe.sp.payment.config.AmqpConfig;

@Service
public class MqMessageService {
    @Autowired
    private RabbitTemplate rabbitTemplate;

    public void send(String localMessageId, OrderMsg message) {
        rabbitTemplate.convertAndSend(AmqpConfig.EXCHANGE_NAME,
                AmqpConfig.ROUTING_KEY_SET_ORDER_PAID, message,
                new CorrelationData(localMessageId));
    }

}
