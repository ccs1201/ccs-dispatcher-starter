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

package br.com.ccs.dispatcher.config;

import br.com.ccs.dispatcher.config.builder.RabbitMQConfigBuilder;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;


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
@AutoConfigureAfter(DispatcherAutoConfig.class)
@ConditionalOnProperty(name = "ccs.dispatcher.enabled", havingValue = "true", matchIfMissing = true)
public class RabbitMQConfig {

    private final Logger log = LoggerFactory.getLogger(RabbitMQConfig.class);
    private final DispatcherProperties properties;
    private final RabbitMQConfigBuilder configBuilder;

    public RabbitMQConfig(@Qualifier("DispatcherProperties") DispatcherProperties properties) {
        this.properties = properties;
        this.configBuilder = RabbitMQConfigBuilder.builder(properties);
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
        return configBuilder.buildConnectionFactory();
    }

    @Bean
    @Primary
    public RabbitAdmin rabbitAdmin(ConnectionFactory connectionFactory) {

        return new RabbitAdmin(connectionFactory);
    }

    @Bean
    @Primary
    public Exchange dispatcherExchange() {
        return configBuilder.buildDispatcherExchange();
    }

    @Bean
    @Qualifier("deadLetterExchange")
    public Exchange deadLetterExchange() {
        return configBuilder.buildDeadLetterExchange();
    }

    @Bean
    @Primary
    public Queue dispatcherQueue() {
        return configBuilder.buildDispatcherQueue();
    }

    @Bean
    @Primary
    public Binding dispatcherQueueBinding(Queue ccsDispatcherQueue,
                                          Exchange ccsDispatcherExchange) {
        return configBuilder.buildDispatcherBinding(ccsDispatcherQueue, ccsDispatcherExchange);
    }

    @Bean
    @Qualifier("deadLetterQueue")
    public Queue deadLetterQueue() {
        return configBuilder.buildDeadLetterQueue();
    }

    @Bean
    @Qualifier("deadLetterQueueBinding")
    public Binding deadLetterQueueBinding(@Qualifier("deadLetterQueue") Queue deadLetterQueue,
                                          @Qualifier("deadLetterExchange") Exchange deadLetterExchange) {
        return configBuilder.buildDeadLetterBinding(deadLetterQueue, deadLetterExchange);
    }

    @Bean
    @Primary
    public RabbitTemplate rabbitTemplate(MessageConverter messageConverter) {
        return configBuilder.buildRabbitTemplate(messageConverter);
    }

    //    @Bean
//    @Primary
//    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(
//            ConnectionFactory connectionFactory,
//            MessageConverter messageConverter) {
//
//        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
//        factory.setConnectionFactory(connectionFactory);
//        factory.setMessageConverter(messageConverter);
//
//        // Configuração de retry
//        RetryInterceptorBuilder<?> retryInterceptorBuilder = RetryInterceptorBuilder.stateless()
//                .maxAttempts(properties.getMaxRetryAttempts())
//                .backOffOptions(
//                        properties.getInitialInterval(),
//                        properties.getMultiplier(),
//                        properties.getMaxInterval()
//                );
//
//        factory.setAdviceChain(retryInterceptorBuilder.build());
//
//        // Configurações adicionais
//        factory.setConcurrentConsumers(
//                Integer.parseInt(properties.getConcurrency().split("-")[0]));
//        factory.setMaxConcurrentConsumers(
//                Integer.parseInt(properties.getConcurrency().split("-")[1]));
//
//        return factory;
//    }
}
