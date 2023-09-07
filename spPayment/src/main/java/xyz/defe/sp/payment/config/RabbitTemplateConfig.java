package xyz.defe.sp.payment.config;

import com.google.common.base.Strings;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.ReturnedMessage;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import xyz.defe.sp.payment.service.LocalMessageService;

@Component
public class RabbitTemplateConfig implements RabbitTemplate.ConfirmCallback,RabbitTemplate.ReturnsCallback {
    @Autowired
    private RabbitTemplate rabbitTemplate;
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private LocalMessageService localMessageService;

    @PostConstruct
    public void init() {
        rabbitTemplate.setConfirmCallback(this);
        rabbitTemplate.setObservationEnabled(true); //to be enabled to send spans to zipkin
    }

    @Override
    public void confirm(CorrelationData correlationData, boolean ack, String cause) {
        String correlationId = correlationData.getId();
        if (ack) {
            log.debug("message sent successful,correlation id={}", correlationId);
        } else {
            if (!Strings.isNullOrEmpty(correlationId)) {
                localMessageService.setRetry(correlationId, 1);
            }
            log.error("message sent failed,{},correlation id={}", cause, correlationId);
        }
    }

    @Override
    public void returnedMessage(ReturnedMessage returned) {
        String correlationId = returned.getMessage().getMessageProperties().getCorrelationId();
        if (!Strings.isNullOrEmpty(correlationId)) {
            localMessageService.setRetry(correlationId, 1);
        }
        log.error("RabbitMQ ReturnCallback:{},{},{},{},{},{}",
                returned.getMessage(), returned.getReplyCode(), returned.getReplyText(),
                returned.getExchange(), returned.getRoutingKey());
    }

}