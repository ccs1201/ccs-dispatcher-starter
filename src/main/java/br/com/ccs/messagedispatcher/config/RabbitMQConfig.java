/*
 * Copyright 2024 Cleber Souza
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package br.com.ccs.messagedispatcher.config;

import br.com.ccs.messagedispatcher.config.properties.MessageDispatcherProperties;
import jakarta.annotation.PostConstruct;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.RetryInterceptorBuilder;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.retry.MessageRecoverer;
import org.springframework.amqp.rabbit.retry.RepublishMessageRecoverer;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.Map;


/**
 * Configuração do RabbitMQ. Cria as exchanges, filas e bindings
 * <p>
 * RabbitMQ configuration. Creates exchanges, queues and bindings
 *
 * @author Cleber Souza
 * @version 1.0
 * @since 09/05/2025
 */

@Configuration
@AutoConfigureAfter(MessageDispatcherAutoConfig.class)
@ConditionalOnProperty(name = "message.dispatcher.enabled", havingValue = "true", matchIfMissing = true)
public class RabbitMQConfig {

    private final Logger log = LoggerFactory.getLogger(RabbitMQConfig.class);
    private final MessageDispatcherProperties properties;

    public RabbitMQConfig(@Qualifier("messageDispatcherProperties") MessageDispatcherProperties properties) {
        this.properties = properties;
    }

    @PostConstruct
    public void init() {
        if (properties.getQueueName() == null || properties.getQueueName().trim().isEmpty()) {
            throw new IllegalStateException("Queue name não pode ser null ou vazio");
        }
        log.debug("Propriedades validadas com sucesso");
        log.debug("Propriedades carregas: ".concat(properties.toString()));

        log.info("RabbitMQConfig inicializado.");
    }

    @Bean
    @Primary
    public ConnectionFactory connectionFactory() {
        log.debug("Configurando ConnectionFactory");
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory();
        connectionFactory.setHost(properties.getHost());
        connectionFactory.setPort(properties.getPort());
        connectionFactory.setUsername(properties.getUsername());
        connectionFactory.setPassword(properties.getPassword());
        connectionFactory.setVirtualHost(properties.getVirtualHost());
        connectionFactory.setPublisherConfirmType(CachingConnectionFactory.ConfirmType.CORRELATED);
        connectionFactory.setPublisherReturns(true);

        log.debug("ConnectionFactory configurada");
        return connectionFactory;
    }

    @Bean
    @Primary
    public RabbitAdmin rabbitAdmin(ConnectionFactory connectionFactory) {
        log.debug("Configurando RabbitAdmin");
        return new RabbitAdmin(connectionFactory);
    }

    @Bean
    @Primary
    public Exchange dispatcherExchange() {
        log.debug("Configurando exchange: " + properties.getExchangeName());
        var ex = ExchangeBuilder
                .topicExchange(properties.getExchangeName())
                .durable(properties.isExchangeDurable())
                .build();

        log.info("Exchange criada: " + ex);
        return ex;
    }

    @Bean
    @Qualifier("deadLetterExchange")
    public Exchange deadLetterExchange() {
        log.debug("Configurando Dead Letter Exchange: " + properties.getDeadLetterExchangeName());
        var dlqEx = ExchangeBuilder
                .topicExchange(properties.getDeadLetterExchangeName())
                .durable(true)
                .build();

        log.info("Dead Letter Exchange criada: " + dlqEx);
        return dlqEx;
    }

    @Bean
    @Primary
    public Queue dispatcherQueue() {
        log.debug("Configurando queue: " + properties.getQueueName());
        var q = QueueBuilder
                .durable(properties.getQueueName())
                .deadLetterRoutingKey(properties.getDeadLetterRoutingKey())
                .deadLetterExchange(properties.getDeadLetterExchangeName())
                .build();

        log.info("Default Queue criada: " + q);
        return q;
    }

    @Bean
    @Primary
    public Binding dispatcherQueueBinding(Queue ccsDispatcherQueue,
                                          Exchange ccsDispatcherExchange) {
        log.debug("Configurando binding: " + properties.getRoutingKey());
        return BindingBuilder
                .bind(ccsDispatcherQueue)
                .to(ccsDispatcherExchange)
                .with(properties.getRoutingKey())
                .noargs();
    }

    @Bean
    @Qualifier("deadLetterQueue")
    public Queue deadLetterQueue() {
        log.debug("Configurando Dead Letter Queue: " + properties.getDeadLetterQueueName());

        var dlqQueue = QueueBuilder
                .durable(properties.getDeadLetterQueueName())
                .build();
        log.info("Dead Letter Queue criada: " + dlqQueue);
        return dlqQueue;
    }

    @Bean
    @Qualifier("deadLetterQueueBinding")
    public Binding deadLetterQueueBinding(@Qualifier("deadLetterQueue") Queue deadLetterQueue,
                                          @Qualifier("deadLetterExchange") Exchange deadLetterExchange) {
        log.debug("Configurando binding da Dead Letter Queue: " + properties.getDeadLetterRoutingKey());
        return BindingBuilder
                .bind(deadLetterQueue)
                .to(deadLetterExchange)
                .with(properties.getDeadLetterRoutingKey())
                .noargs();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory, MessageConverter messageConverter) {
        log.debug("Configurando RabbitTemplate");
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(messageConverter);

        // Configurando exchange e routing key padrão
        template.setExchange(properties.getExchangeName());
        template.setRoutingKey(properties.getRoutingKey());

        // Configurando confirmação de publicação
        template.setMandatory(true);
        template.setReplyTimeout(properties.getReplyTimeOut());

        template.setConfirmCallback((correlationData, ack, cause) -> {
            if (ack) {
                log.info("Mensagem confirmada: " + correlationData);
            } else {
                log.error("Mensagem não confirmada: " + cause);
            }
        });

        // Configurando retorno de mensagem
        template.setReturnsCallback(returned -> log.info("Mensagem retornada: " + returned.getMessage() +
                " code: " + returned.getReplyCode() +
                " reason: " + returned.getReplyText()));

        log.info("RabbitTemplate configurado com exchange: " + properties.getExchangeName() +
                " e routing key: " + properties.getRoutingKey());

        return template;
    }

    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(ConnectionFactory connectionFactory,
                                                                               MessageConverter messageConverter,
                                                                               MessageRecoverer messageRecoverer) {

        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(messageConverter);
        factory.setPrefetchCount(properties.getPrefetchCount());
        factory.setDefaultRequeueRejected(false);

        // Configuração de retry
        var retryInterceptorBuilder = RetryInterceptorBuilder.stateless()
                .maxAttempts(properties.getMaxRetryAttempts())
                .backOffOptions(
                        properties.getInitialInterval(),
                        properties.getMultiplier(),
                        properties.getMaxInterval()
                )
                .recoverer(messageRecoverer);
//                .recoverer(new RejectAndDontRequeueRecoverer());

        factory.setAdviceChain(retryInterceptorBuilder.build());

        factory.setConcurrentConsumers(
                Integer.parseInt(properties.getConcurrency().split("-")[0]));
        factory.setMaxConcurrentConsumers(
                Integer.parseInt(properties.getConcurrency().split("-")[1]));

        return factory;
    }

    @Bean
    public MessageRecoverer messageRecoverer(RabbitTemplate rabbitTemplate) {
        return new RepublishMessageRecoverer(rabbitTemplate,
                properties.getDeadLetterExchangeName(),
                properties.getDeadLetterRoutingKey()) {
            @Override
            protected Map<String, Object> additionalHeaders(Message message, Throwable cause) {

                var rootCause = ExceptionUtils.getRootCause(cause);

                log.error("Enviando mensagem para dead letter queue.", rootCause);

                Map<String, Object> headers = new HashMap<>();
                headers.put("x-exception-message", rootCause.getLocalizedMessage());
                headers.put("x-exception-type", rootCause.getClass().getName());
                headers.put("x-original-exchange", message.getMessageProperties().getReceivedExchange());
                headers.put("x-failed-at", OffsetDateTime.now());
                return headers;
            }
        };
    }
}
