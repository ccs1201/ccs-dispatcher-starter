package br.com.ccs.dispatcher.config.builder;

import br.com.ccs.dispatcher.config.DispatcherProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.MessageConverter;

import java.util.Objects;

public class RabbitMQConfigBuilder {
    private final Logger log = LoggerFactory.getLogger(RabbitMQConfigBuilder.class);
    private final DispatcherProperties properties;
    private ConnectionFactory connectionFactory;

    private RabbitMQConfigBuilder(DispatcherProperties properties) {
        Objects.requireNonNull(properties, "Properties não pode ser null");
        this.properties = properties;
    }

    public static RabbitMQConfigBuilder builder(DispatcherProperties properties) {
        return new RabbitMQConfigBuilder(properties);
    }

    public ConnectionFactory buildConnectionFactory() {
        log.debug("Configurando ConnectionFactory");
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory();
        connectionFactory.setHost(properties.getHost());
        connectionFactory.setPort(properties.getPort());
        connectionFactory.setUsername(properties.getUsername());
        connectionFactory.setPassword(properties.getPassword());
        connectionFactory.setVirtualHost(properties.getVirtualHost());
        connectionFactory.setPublisherConfirmType(CachingConnectionFactory.ConfirmType.CORRELATED);
        connectionFactory.setPublisherReturns(true);

        this.connectionFactory = connectionFactory;
        log.debug("ConnectionFactory configurada");
        return connectionFactory;
    }

    public Exchange buildDispatcherExchange() {
        log.debug("Configurando exchange: " + properties.getExchangeName());
        var exchange = ExchangeBuilder
                .topicExchange(properties.getExchangeName())
                .durable(properties.isExchangeDurable())
                .build();
        log.info("Exchange criada: " + exchange);
        return exchange;
    }

    public Exchange buildDeadLetterExchange() {
        log.debug("Configurando Dead Letter Exchange: " + properties.getDeadLetterExchangeName());
        var dlExchange = ExchangeBuilder
                .topicExchange(properties.getDeadLetterExchangeName())
                .durable(true)
                .build();
        log.info("Dead Letter Exchange criada: " + dlExchange);
        return dlExchange;
    }

    public Queue buildDispatcherQueue() {
        log.debug("Configurando queue: " + properties.getQueueName());
        var queue = QueueBuilder
                .durable(properties.getQueueName())
                .deadLetterRoutingKey(properties.getDeadLetterRoutingKey())
                .deadLetterExchange(properties.getDeadLetterExchangeName())
                .build();
        log.info("Default Queue criada: " + queue);
        return queue;
    }

    public Queue buildDeadLetterQueue() {
        log.debug("Configurando Dead Letter Queue: " + properties.getDeadLetterQueueName());
        var dlQueue = QueueBuilder
                .durable(properties.getDeadLetterQueueName())
                .build();
        log.info("Dead Letter Queue criada: " + dlQueue);
        return dlQueue;
    }

    public Binding buildDispatcherBinding(Queue queue, Exchange exchange) {
        log.debug("Configurando binding: " + properties.getRoutingKey());
        return BindingBuilder
                .bind(queue)
                .to(exchange)
                .with(properties.getRoutingKey())
                .noargs();
    }

    public Binding buildDeadLetterBinding(Queue dlQueue, Exchange dlExchange) {
        log.debug("Configurando binding da Dead Letter Queue: " + properties.getDeadLetterRoutingKey());
        return BindingBuilder
                .bind(dlQueue)
                .to(dlExchange)
                .with(properties.getDeadLetterRoutingKey())
                .noargs();
    }

    public RabbitTemplate buildRabbitTemplate(MessageConverter messageConverter) {
        if (connectionFactory == null) {
            buildConnectionFactory();
        }

        log.debug("Configurando RabbitTemplate");
        RabbitTemplate template = new RabbitTemplate(connectionFactory);

        template.setMessageConverter(messageConverter);

        template.setExchange(properties.getExchangeName());
        template.setRoutingKey(properties.getRoutingKey());
        template.setMandatory(true);

        template.setConfirmCallback((correlationData, ack, cause) -> {
            if (ack) {
                log.info("Mensagem confirmada: " + correlationData);
            } else {
                log.error("Mensagem não confirmada: " + cause);
            }
        });

        template.setReturnsCallback(returned -> log.info("Mensagem retornada: " + returned.getMessage() +
                " code: " + returned.getReplyCode() +
                " reason: " + returned.getReplyText()));

        log.info("RabbitTemplate configurado com exchange: " + properties.getExchangeName() +
                " e routing key: " + properties.getRoutingKey());

        return template;
    }
}
