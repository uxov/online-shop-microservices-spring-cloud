package xyz.defe.sp.payment.config;

import com.google.common.base.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import xyz.defe.sp.payment.service.LocalMessageService;

import javax.annotation.PostConstruct;

@Component
public class RabbitTemplateConfig implements RabbitTemplate.ConfirmCallback,RabbitTemplate.ReturnCallback {
    @Autowired
    private RabbitTemplate rabbitTemplate;
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private LocalMessageService localMessageService;

    @PostConstruct
    public void init() {
        rabbitTemplate.setConfirmCallback(this);
    }

    @Override
    public void confirm(CorrelationData correlationData, boolean ack, String cause) {
        String correlationId = correlationData.getId();
        if (ack) {
            log.info("message sent successful,correlation id={}", correlationId);
        } else {
            if (!Strings.isNullOrEmpty(correlationId)) {
                localMessageService.setRetry(correlationId, 1);
            }
            log.error("message sent failed,{},correlation id={}", cause, correlationId);
        }
    }

    @Override
    public void returnedMessage(Message message, int replyCode, String replyText, String exchange, String routingKey) {
        String correlationId = message.getMessageProperties().getCorrelationId();
        if (!Strings.isNullOrEmpty(correlationId)) {
            localMessageService.setRetry(correlationId, 1);
        }
        log.error("RabbitMQ ReturnCallback:{},{},{},{},{},{}", message, replyCode, replyText, exchange, routingKey);
    }

}