package xyz.defe.sp.order.config;

import org.springframework.amqp.rabbit.AsyncRabbitTemplate;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class RabbitTemplateConfig {

    @Bean
    public AsyncRabbitTemplate asyncRabbitTemplate(RabbitTemplate rabbitTemplate) {
        return new AsyncRabbitTemplate(rabbitTemplate);
    }

    @Bean
    public RabbitTemplate rabbitTemplate(final ConnectionFactory connectionFactory) {
        CachingConnectionFactory cachingConnectionFactory = (CachingConnectionFactory) connectionFactory;
        cachingConnectionFactory.getRabbitConnectionFactory().setRequestedChannelMax(4096); //same config as RabbitMQ Server
        final RabbitTemplate rabbitTemplate = new RabbitTemplate(cachingConnectionFactory);
        return rabbitTemplate;
    }
}