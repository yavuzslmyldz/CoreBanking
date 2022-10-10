package com.tuum.core.banking.config;

import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MessageQueueConfig {

    @Value("${core.banking.rabbitmq.exchange}")
    String exchange;
    @Value("${core.banking.rabbitmq.account.queue}")
    String accountQueue;

    @Value("${core.banking.rabbitmq.transaction.queue}")
    String transactionQueue;

    @Value("${core.banking.rabbitmq.account.routing.key}")
    String accountRoutingKey;

    @Value("${core.banking.rabbitmq.transaction.routing.key}")
    String transactionRoutingKey;


    @Bean
    Queue accountQueue() {
        return new Queue(accountQueue, false);
    }

    @Bean
    Queue transactionQueue() {
        return new Queue(transactionQueue, false);
    }
    @Bean
    DirectExchange exchange() {
        return new DirectExchange(exchange);
    }

    @Bean
    Binding accountBinding() {
        return BindingBuilder.bind(accountQueue()).to(exchange()).with(accountRoutingKey);
    }

    @Bean
    Binding transactionBinding() {
        return BindingBuilder.bind(transactionQueue()).to(exchange()).with(transactionRoutingKey);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }


    @Bean
    public AmqpTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        final RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(jsonMessageConverter());
        return rabbitTemplate;
    }

}