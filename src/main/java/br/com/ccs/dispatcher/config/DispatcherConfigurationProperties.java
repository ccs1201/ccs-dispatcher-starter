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

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import java.util.Arrays;


/**
 * Propriedades de configuração do Dispatcher.
 * Configuration properties for the Dispatcher.
 * <p>
 * As propriedades podem ser configuradas no arquivo application.properties ou application.yml ou através de variáveis de ambiente.
 * Properties can be configured in the application.properties or application.yml file or through environment variables.
 * <p>
 * Para configurar as propriedades, basta adicionar o prefixo "ccs.dispatcher" antes do nome da propriedade.
 * To configure the properties, just add the prefix "ccs.dispatcher" before the property name.
 * <p>
 * Exemplo:
 * <p>
 * ccs.dispatcher.host=127.0.0.1
 * <p>
 * ccs.dispatcher.port=5672
 * <p>
 * ccs.dispatcher.username=guest
 * <p>
 * ccs.dispatcher.password=guest
 * <p>
 * ccs.dispatcher.virtualHost=/
 * <p>
 * ccs.dispatcher.exchangeName=ccs.dispatcher.ex
 * <p>
 * ccs.dispatcher.exchangeType=topic
 * <p>
 * ccs.dispatcher.queueName=ccs.dispatcher.queue
 * <p>
 * ccs.dispatcher.routingKey=ccs.dispatcher.queue
 * <p>
 * ccs.dispatcher.concurrency=1-10
 * <p>
 * ccs.dispatcher.queueDurable=true
 * <p>
 * ccs.dispatcher.deadLetterQueueName=ccs.dispatcher.queue.dlq
 * <p>
 * ccs.dispatcher.deadLetterExchangeName=ccs.dispatcher.ex.dlx
 * <p>
 * ccs.dispatcher.deadLetterRoutingKey=ccs.dispatcher.queue.dlq
 * <p>
 * ccs.dispatcher.mapped.headers
 *
 * @author Cleber Souza
 * @version 1.0
 * @since 09/05/2025
 */

@AutoConfigureBefore(CcsDispatcherAutoConfig.class)
@Component("ccsDispatcherProperties")
@ConfigurationProperties(prefix = "ccs.dispatcher")
@Validated
public class DispatcherConfigurationProperties {

    @PostConstruct
    public void init() {
        final Logger log = LoggerFactory.getLogger(DispatcherConfigurationProperties.class);
        // Se não foram configurados, criar nomes padrão para DLQ
        if (deadLetterQueueName == null) {
            deadLetterQueueName = queueName.concat(".dlq");
        }
        if (deadLetterExchangeName == null) {
            deadLetterExchangeName = exchangeName.concat(".dlx");
        }

        if (deadLetterRoutingKey == null) {
            deadLetterRoutingKey = deadLetterQueueName;
        }

        if (routingKey == null) {
            routingKey = queueName;
        }

        if (mappedHeaders != null) {
            mappedHeadersArray = Arrays.stream(mappedHeaders.split(","))
                    .filter(s -> !s.isEmpty())
                    .map(String::trim)
                    .toArray(String[]::new);
        }

        log.debug("DispatcherProperties inicializado com os seguintes valores:" + this);
    }

    @Value("${ccs.dispatcher.mapped.headers}")
    private String mappedHeaders;
    private String[] mappedHeadersArray;

    /**
     * Ip ou Nome do Host do RabbitMQ
     */
    private String host = "localhost";

    /**
     * Porta do RabbitMQ. Padrão é 5672
     */
    private int port = 5672;

    /**
     * Usuário do RabbitMQ. Padrão é 'guest'
     */
    private String username = "guest";

    /**
     * Senha do RabbitMQ. Padrão é 'guest'
     */
    private String password = "guest";

    /**
     * Virtual host do RabbitMQ. Padrão é '/'
     */
    private String virtualHost = "/";

    /**
     * Nome da exchange. Padrão é 'ccs.dispatcher.ex'
     */
    private String exchangeName = "ccs.dispatcher.ex";

    /**
     * Tipo da exchange. Padrão é 'topic'
     */
    private String exchangeType = "topic";

    /**
     * Nome da fila RabbitMQ. Se não configurado, usa o nome da aplicação
     */
    @Value("${spring.application.name}")
    private String queueName;

    /**
     * Routing key para binding. Padrão é mesmo valor de {queueName}
     */
    private String routingKey;

    /**
     * Configuração de concorrência do consumidor. Padrão é "1-10"
     */
    private String concurrency = "1-10";

    /**
     * Se true, a fila será durável. Padrão é true
     */
    private boolean queueDurable = true;

    /**
     * Se true, a exchange será durável. Padrão é true
     */
    private boolean exchangeDurable = true;

    /**
     * Nome da exchange de dead letter. Padrão é '{exchange}.dlq'
     */
    private String deadLetterExchangeName;

    /**
     * Nome da fila de dead letter. Padrão é '{queue}.dlq'
     */
    private String deadLetterQueueName;

    /**
     * Routing key para dead letter. Padrão é '{deadLetterQueueName}'
     */
    private String deadLetterRoutingKey;

    /**
     * Se true, a dead letter exchange será durável. Padrão é true
     */
    private boolean deadLetterExchangeDurable = true;

    /**
     * Retentativas máximas antes de enviar para a dead letter. Padrão é 3
     */
    private int maxRetryAttempts = 3;

    /**
     * Intervalo inicial entre as tentativas. Padrão é 1000ms
     */
    private int initialInterval = 1000;

    /**
     * Multiplicador do intervalo entre as tentativas. Padrão é 2
     */
    private int multiplier = 2;

    /**
     * Intervalo máximo entre as tentativas. Padrão é 10000ms
     */
    private int maxInterval = 10000;


    public String getQueueName() {
        return queueName;
    }

    public void setQueueName(String queueName) {
        this.queueName = queueName;
    }

    public String getExchangeName() {
        return exchangeName;
    }

    public void setExchangeName(String exchangeName) {
        this.exchangeName = exchangeName;
    }

    public String getExchangeType() {
        return exchangeType;
    }

    public void setExchangeType(String exchangeType) {
        this.exchangeType = exchangeType;
    }

    public String getRoutingKey() {
        return routingKey;
    }

    public void setRoutingKey(String routingKey) {
        this.routingKey = routingKey;
    }

    public String getConcurrency() {
        return concurrency;
    }

    public void setConcurrency(String concurrency) {
        this.concurrency = concurrency;
    }

    public boolean isQueueDurable() {
        return queueDurable;
    }

    public void setQueueDurable(boolean queueDurable) {
        this.queueDurable = queueDurable;
    }

    public boolean isExchangeDurable() {
        return exchangeDurable;
    }

    public void setExchangeDurable(boolean exchangeDurable) {
        this.exchangeDurable = exchangeDurable;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getVirtualHost() {
        return virtualHost;
    }

    public void setVirtualHost(String virtualHost) {
        this.virtualHost = virtualHost;
    }

    public String getDeadLetterQueueName() {
        return deadLetterQueueName;
    }

    public void setDeadLetterQueueName(String deadLetterQueueName) {
        this.deadLetterQueueName = deadLetterQueueName;
    }

    public String getDeadLetterExchangeName() {
        return deadLetterExchangeName;
    }

    public void setDeadLetterExchangeName(String deadLetterExchangeName) {
        this.deadLetterExchangeName = deadLetterExchangeName;
    }

    public String getDeadLetterRoutingKey() {
        return deadLetterRoutingKey;
    }

    public void setDeadLetterRoutingKey(String deadLetterRoutingKey) {
        this.deadLetterRoutingKey = deadLetterRoutingKey;
    }

    public boolean isDeadLetterExchangeDurable() {
        return deadLetterExchangeDurable;
    }

    public void setDeadLetterExchangeDurable(boolean deadLetterExchangeDurable) {
        this.deadLetterExchangeDurable = deadLetterExchangeDurable;
    }

    public int getMaxRetryAttempts() {
        return maxRetryAttempts;
    }

    public void setMaxRetryAttempts(int maxRetryAttempts) {
        this.maxRetryAttempts = maxRetryAttempts;
    }

    public int getInitialInterval() {
        return initialInterval;
    }

    public void setInitialInterval(int initialInterval) {
        this.initialInterval = initialInterval;
    }

    public int getMultiplier() {
        return multiplier;
    }

    public void setMultiplier(int multiplier) {
        this.multiplier = multiplier;
    }

    public int getMaxInterval() {
        return maxInterval;
    }

    public void setMaxInterval(int maxInterval) {
        this.maxInterval = maxInterval;
    }

    public String[] getMappedHeaders() {
        return mappedHeadersArray;
    }

    @Override
    public String toString() {
        return "DispatcherConfigurationProperties{" +
                "host='" + host + '\'' +
                ", port=" + port +
                ", virtualHost='" + virtualHost + '\'' +
                ", exchangeName='" + exchangeName + '\'' +
                ", exchangeType='" + exchangeType + '\'' +
                ", exchangeDurable=" + exchangeDurable +
                ", queueName='" + queueName + '\'' +
                ", routingKey='" + routingKey + '\'' +
                ", queueDurable=" + queueDurable +
                ", concurrency='" + concurrency + '\'' +
                ", deadLetterExchangeName='" + deadLetterExchangeName + '\'' +
                ", deadLetterExchangeDurable=" + deadLetterExchangeDurable +
                ", deadLetterQueueName='" + deadLetterQueueName + '\'' +
                ", deadLetterRoutingKey='" + deadLetterRoutingKey + '\'' +
                ", maxRetryAttempts=" + maxRetryAttempts +
                ", initialInterval=" + initialInterval +
                ", multiplier=" + multiplier +
                ", maxInterval=" + maxInterval +
                ", mappedHeaders=" + mappedHeaders +
                '}';
    }
}
