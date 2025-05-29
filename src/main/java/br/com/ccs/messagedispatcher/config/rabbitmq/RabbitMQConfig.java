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

package br.com.ccs.messagedispatcher.config.rabbitmq;

import br.com.ccs.messagedispatcher.config.properties.MessageDispatcherProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.ExchangeBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.rabbit.config.RetryInterceptorBuilder;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.retry.MessageRecoverer;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.retry.interceptor.RetryOperationsInterceptor;


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
//@AutoConfigureAfter(MessageDispatcherAutoConfig.class)
@ConditionalOnProperty(name = "message.dispatcher.enabled", havingValue = "true", matchIfMissing = true)
public class RabbitMQConfig {

    private final Logger log = LoggerFactory.getLogger(RabbitMQConfig.class);

    @Bean
    @Primary
    protected Exchange dispatcherExchange(MessageDispatcherProperties properties) {
        log.debug("Configurando exchange: {}", properties.getExchangeName());
        var ex = ExchangeBuilder
                .topicExchange(properties.getExchangeName())
                .durable(properties.isExchangeDurable())
                .build();

        log.info("Exchange criada: {}", ex);
        return ex;
    }

    @Bean
    @Qualifier("deadLetterExchange")
    protected Exchange deadLetterExchange(MessageDispatcherProperties properties) {
        log.debug("Configurando Dead Letter Exchange: {}", properties.getDeadLetterExchangeName());
        var dlqEx = ExchangeBuilder
                .topicExchange(properties.getDeadLetterExchangeName())
                .durable(properties.isDeadLetterExchangeDurable())
                .build();

        log.info("Dead Letter Exchange criada: {} ", dlqEx);
        return dlqEx;
    }

    @Bean
    @Primary
    protected Queue dispatcherQueue(MessageDispatcherProperties properties) {
        log.debug("Configurando queue: {}", properties.getQueueName());
        var q = QueueBuilder
                .durable(properties.getQueueName())
                .deadLetterExchange(properties.getDeadLetterExchangeName())
                .deadLetterRoutingKey(properties.getDeadLetterRoutingKey())
                .build();

        log.info("Default Queue criada: {}", q);
        return q;
    }

    @Bean
    @Primary
    protected Binding dispatcherQueueBinding(Queue ccsDispatcherQueue,
                                             Exchange ccsDispatcherExchange,
                                             MessageDispatcherProperties properties) {
        log.debug("Configurando binding: {}", properties.getRoutingKey());
        return BindingBuilder
                .bind(ccsDispatcherQueue)
                .to(ccsDispatcherExchange)
                .with(properties.getRoutingKey())
                .noargs();
    }

    @Bean
    @Qualifier("deadLetterQueue")
    protected Queue deadLetterQueue(MessageDispatcherProperties properties) {
        log.debug("Configurando Dead Letter Queue: {}", properties.getDeadLetterQueueName());

        var dlqQueue = QueueBuilder
                .durable(properties.getDeadLetterQueueName())
                .build();
        log.info("Dead Letter Queue criada: {}", dlqQueue);
        return dlqQueue;
    }

    @Bean
    @Qualifier("deadLetterQueueBinding")
    protected Binding deadLetterQueueBinding(@Qualifier("deadLetterQueue") Queue deadLetterQueue,
                                             @Qualifier("deadLetterExchange") Exchange deadLetterExchange,
                                             MessageDispatcherProperties properties) {
        log.debug("Configurando binding da Dead Letter Queue: {}", properties.getDeadLetterRoutingKey());
        return BindingBuilder
                .bind(deadLetterQueue)
                .to(deadLetterExchange)
                .with(properties.getDeadLetterRoutingKey())
                .noargs();
    }

    @Bean
    protected SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(ConnectionFactory connectionFactory,
                                                                                  MessageConverter messageConverter,
                                                                                  RetryOperationsInterceptor retryOperationsInterceptor,
                                                                                  MessageDispatcherProperties properties) {

        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(messageConverter);
        factory.setDefaultRequeueRejected(false);
        factory.setAdviceChain(retryOperationsInterceptor);

        //configura o número de mensagens que serão consumidas de uma vez
        factory.setPrefetchCount(properties.getPrefetchCount());
        //configura a concorrência de consumidores
        factory.setConcurrentConsumers(
                Integer.parseInt(properties.getConcurrency().split("-")[0]));
        factory.setMaxConcurrentConsumers(
                Integer.parseInt(properties.getConcurrency().split("-")[1]));

        return factory;
    }

    @Bean
    protected RetryOperationsInterceptor retryOperationsInterceptor(MessageRecoverer messageRecoverer, MessageDispatcherProperties properties) {
        return RetryInterceptorBuilder.stateless()
                .maxAttempts(properties.getMaxRetryAttempts())
                .backOffOptions(
                        properties.getInitialInterval(),
                        properties.getMultiplier(),
                        properties.getMaxInterval()
                )
                .recoverer(messageRecoverer).build();
    }
}
