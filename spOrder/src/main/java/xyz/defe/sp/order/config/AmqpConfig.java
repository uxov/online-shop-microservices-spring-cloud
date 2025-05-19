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
    Queue deductQuantityRequest() {
        return new Queue(Const.QUEUE_DEDUCT_QUANTITY_REQUEST, true);
    }

    @Bean
    Queue deductQuantityReply() {
        return new Queue(Const.QUEUE_DEDUCT_QUANTITY_REPLY, true);
    }

    @Bean
    Binding requestBinding(Queue deductQuantityRequest, DirectExchange exchange) {
        return BindingBuilder.bind(deductQuantityRequest).to(exchange)
                .with(Const.ROUTING_KEY_DEDUCT_QUANTITY_REQUEST);
    }

    @Bean
    Binding responseBinding(Queue deductQuantityReply, DirectExchange exchange) {
        return BindingBuilder.bind(deductQuantityReply).to(exchange)
                .with(Const.ROUTING_KEY_DEDUCT_QUANTITY_REPLY);
    }
}
