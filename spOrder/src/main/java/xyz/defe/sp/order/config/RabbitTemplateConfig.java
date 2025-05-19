package xyz.defe.sp.order.config;

import org.springframework.amqp.rabbit.AsyncRabbitTemplate;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.DirectReplyToMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.amqp.support.converter.SimpleMessageConverter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import xyz.defe.sp.common.Const;

import java.util.List;

@Component
public class RabbitTemplateConfig {

    @Bean
    public SimpleMessageConverter simpleMessageConverter() {
        SimpleMessageConverter converter = new SimpleMessageConverter();
        converter.setAllowedListPatterns(List.of("xyz.defe.sp.*", "java.util.*"));
        return converter;
    }

    @Bean
    public RabbitTemplate rabbitTemplate(final ConnectionFactory connectionFactory, SimpleMessageConverter converter) {
        CachingConnectionFactory cachingConnectionFactory = (CachingConnectionFactory) connectionFactory;
//        cachingConnectionFactory.getRabbitConnectionFactory().setRequestedChannelMax(4096); //same config as RabbitMQ Server
//        cachingConnectionFactory.setConnectionCacheSize(100);
//        cachingConnectionFactory.setChannelCheckoutTimeout(1000);
        final RabbitTemplate rabbitTemplate = new RabbitTemplate(cachingConnectionFactory);
        rabbitTemplate.setObservationEnabled(true); //to be enabled to send spans to zipkin
        rabbitTemplate.setMessageConverter(converter);
        return rabbitTemplate;
    }

    @Bean
    public AsyncRabbitTemplate asyncRabbitTemplate(RabbitTemplate rabbitTemplate) {
        return new AsyncRabbitTemplate(rabbitTemplate);
    }

    @Bean("orderTemplate")
    public AsyncRabbitTemplate orderTemplate(ConnectionFactory connectionFactory, SimpleMessageConverter converter) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setExchange(Const.EXCHANGE_ORDER);
        template.setRoutingKey(Const.ROUTING_KEY_DEDUCT_QUANTITY_REQUEST);
        template.setMessageConverter(converter);
        template.setObservationEnabled(true);

        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer(connectionFactory);
        container.setQueueNames(Const.QUEUE_DEDUCT_QUANTITY_REPLY); //set reply queue
        container.setConcurrentConsumers(10);  //set reply queue concurrency Consumers

//        MessageListenerAdapter adapter = new MessageListenerAdapter(converter);
//        container.setMessageListener(adapter);

        AsyncRabbitTemplate asyncTemplate = new AsyncRabbitTemplate(template, container);
        asyncTemplate.setReceiveTimeout(30000);
        return asyncTemplate;
    }
}