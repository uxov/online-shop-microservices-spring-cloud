package xyz.defe.sp.payment.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import xyz.defe.sp.common.Const;

@Configuration
public class AmqpConfig {
    public static final String EXCHANGE_NAME = "payment.exchange";
    public static final String ROUTING_KEY_SET_ORDER_PAID = "orderProcess.setOrderPaid";

    @Bean
    DirectExchange exchange() {
        return new DirectExchange (EXCHANGE_NAME);
    }

    @Bean
    Queue queueForSetOrderPaid() {
        return new Queue(Const.QUEUE_SET_ORDER_PAID);
    }

    @Bean
    Binding binding(Queue queueForSetOrderPaid, DirectExchange exchange) {
        return BindingBuilder.bind(queueForSetOrderPaid).to(exchange).with(ROUTING_KEY_SET_ORDER_PAID);
    }

}
