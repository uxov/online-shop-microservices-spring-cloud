package xyz.defe.sp.order.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import xyz.defe.sp.common.Const;

@Configuration
public class AmqpConfig {
    @Bean
    DirectExchange exchange() {
        return new DirectExchange (Const.EXCHANGE_ORDER);
    }

    @Bean
    Queue queueForDeductQuantityRequest() {
        return new Queue(Const.QUEUE_DEDUCT_QUANTITY_REQUEST);
    }

    @Bean
    Queue queueForDeductQuantityReply() {
        return new Queue(Const.QUEUE_DEDUCT_QUANTITY_REPLY);
    }

    @Bean
    Binding binding(Queue queueForDeductQuantityRequest, DirectExchange exchange) {
        return BindingBuilder.bind(queueForDeductQuantityRequest).to(exchange)
                .with(Const.ROUTING_KEY_DEDUCT_QUANTITY_REQUEST);
    }
}
