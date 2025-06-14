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

package br.com.messagedispatcher.config.rabbitmq;

import br.com.messagedispatcher.config.properties.MessageDispatcherProperties;
import br.com.messagedispatcher.util.factory.ExchangeFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Declarables;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;


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
@ConditionalOnProperty(name = "message.dispatcher.default-listener-enabled", havingValue = "true", matchIfMissing = true)
public class ExchangesQueuesBindingConfigAutoConfig {

    private final Logger log = LoggerFactory.getLogger(ExchangesQueuesBindingConfigAutoConfig.class);

    @Bean
    public Declarables defaultExchangeAndQueue(MessageDispatcherProperties properties) {
        var exchange = ExchangeFactory
                .buildExchange(properties.getExchangeName(),
                        properties.isExchangeDurable(),
                        properties.getExchangeType(),
                        properties.getExchangeConsistentHashArguments());

        var queue = QueueBuilder
                .durable(properties.getQueueName())
                .deadLetterExchange(properties.getDeadLetterExchangeName())
                .deadLetterRoutingKey(properties.getDeadLetterRoutingKey())
                .build();

        var binding = BindingBuilder
                .bind(queue)
                .to(exchange)
                .with(properties.getRoutingKey())
                .noargs();

        log("Default Exchange and Queue", exchange, queue, binding);

        return new Declarables(
                List.of(exchange, queue, binding)
        );
    }

    @Bean
    public Declarables deadLetterExchangeAndQueue(MessageDispatcherProperties properties) {
        var exchange = ExchangeFactory
                .buildExchange(properties.getDeadLetterExchangeName(),
                        properties.isDeadLetterExchangeDurable(),
                        properties.getDeadLetterExchangeType(),
                        properties.getDeadLetterExchangeConsistentHashArguments());

        var queue = QueueBuilder
                .durable(properties.getDeadLetterQueueName())
                .build();

        var binding = BindingBuilder
                .bind(queue)
                .to(exchange)
                .with(properties.getDeadLetterRoutingKey())
                .noargs();

        log("Dead Letter Exchange and Queue", exchange, queue, binding);

        return new Declarables(
                List.of(exchange, queue, binding)
        );
    }

    private void log(String info, Exchange exchange, Queue queue, Binding binding) {
        log.debug("Criando Exchange, Queue e Binding para {}", info);
        log.debug("Exchange {} criada", exchange.getName());
        log.debug("Queue {} criada", queue.getName());
        log.debug("Binding com {} criado", binding.getExchange());
    }
}
