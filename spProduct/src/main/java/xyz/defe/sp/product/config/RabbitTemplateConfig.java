package xyz.defe.sp.product.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.ReturnedMessage;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;

@Component
public class RabbitTemplateConfig implements RabbitTemplate.ConfirmCallback,RabbitTemplate.ReturnsCallback {
    @Autowired
    private RabbitTemplate rabbitTemplate;
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @PostConstruct
    public void init() {
        rabbitTemplate.setConfirmCallback(this);
        rabbitTemplate.setObservationEnabled(true); //to be enabled to send spans to zipkin
    }

    @Override
    public void confirm(CorrelationData correlationData, boolean ack, String cause) {
        if (ack) {
            log.debug("message sent successful,correlation id={}", correlationData.getId());
        } else {
            log.error("message sent failed,{},correlation id={}", cause, correlationData.getId());
        }
    }

    @Override
    public void returnedMessage(ReturnedMessage returned) {
        log.error("RabbitMQ ReturnCallback:{},{},{},{},{},{}",
                returned.getMessage(), returned.getReplyCode(), returned.getReplyText(),
                returned.getExchange(), returned.getRoutingKey());
    }
}