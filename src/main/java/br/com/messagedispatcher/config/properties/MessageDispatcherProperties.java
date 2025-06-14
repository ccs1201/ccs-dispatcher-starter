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

package br.com.messagedispatcher.config.properties;

import br.com.messagedispatcher.config.MessageDispatcherAutoConfig;
import br.com.messagedispatcher.config.rabbitmq.RabbitTemplateAutoConfig;
import br.com.messagedispatcher.constants.MessageDispatcherConstants;
import br.com.messagedispatcher.constants.MessageDispatcherConstants.Exchange;
import br.com.messagedispatcher.exceptions.MessageDispatcherBeanResolutionException;
import jakarta.annotation.PostConstruct;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import java.util.Arrays;
import java.util.Map;

import static java.util.Objects.isNull;


/**
 * Propriedades de configuração do Dispatcher.
 * Configuration properties for the Dispatcher.
 * <p>
 * As propriedades podem ser configuradas no arquivo application.properties ou application.yml ou através de variáveis de ambiente.
 * Properties can be configured in the application.properties or application.yml file or through environment variables.
 * <p>
 * Para configurar as propriedades, basta adicionar o prefixo "message.dispatcher" antes do nome da propriedade.
 * To configure the properties, just add the prefix "message.dispatcher" before the property name.
 * <p>
 * Exemplo:
 * <p>
 * message.dispatcher.host=127.0.0.1
 * <p>
 * message.dispatcher.port=5672
 * <p>
 * message.dispatcher.username=guest
 * <p>
 * message.dispatcher.password=guest
 * <p>
 * message.dispatcher.virtualHost=/
 * <p>
 * message.dispatcher.exchangeName=message.dispatcher.ex
 * <p>
 * message.dispatcher.exchangeType=topic
 * <p>
 * message.dispatcher.queueName=message.dispatcher.queue
 * <p>
 * message.dispatcher.routingKey=message.dispatcher.queue
 * <p>
 * message.dispatcher.concurrency=1-10
 * <p>
 * message.dispatcher.queueDurable=true
 * <p>
 * message.dispatcher.deadLetterQueueName=message.dispatcher.queue.dlq
 * <p>
 * message.dispatcher.deadLetterExchangeName=message.dispatcher.ex.dlx
 * <p>
 * message.dispatcher.deadLetterRoutingKey=message.dispatcher.queue.dlq
 * <p>
 * message.dispatcher.mapped.headers
 * <p>
 * message.dispatcher.prefetchCount default 10
 * <p>
 * message.dispatcher.receive-timeout default 10
 *
 * @author Cleber Souza
 * @version 1.0
 * @since 09/05/2025
 */

@AutoConfigureBefore({MessageDispatcherAutoConfig.class, RabbitTemplateAutoConfig.class})
@Component("messageDispatcherProperties")
@ConfigurationProperties(prefix = "message.dispatcher")
@Validated
public class MessageDispatcherProperties {

    @PostConstruct
    public void init() {
        final Logger log = LoggerFactory.getLogger(MessageDispatcherProperties.class);
        // Se não foram configurados, criar nomes padrão para DLQ
        if (isNull(deadLetterQueueName)) {
            deadLetterQueueName = queueName.concat(".dlq");
        }
        if (isNull(deadLetterExchangeName)) {
            deadLetterExchangeName = exchangeName.replace(".ex", ".dlx");
        }

        if (isNull(deadLetterRoutingKey)) {
            deadLetterRoutingKey = deadLetterQueueName;
        }

        if (isNull(routingKey)) {
            routingKey = queueName;
        }

        if (this.exchangeType == MessageDispatcherConstants.Exchange.CONSISTENT_HASH) {
            if (isNull(this.exchangeConsistentHashArguments)) {
                throw new MessageDispatcherBeanResolutionException("Quando a exchange é do tipo ConsistenHash é " +
                        "obrigatório informar o argumento exchangeConsistentHashArguments");
            }
        }

        this.queueName = this.queueName.concat(".inbox");

        getMappedHeaders();

        log.debug("MessageDispatcherProperties inicializado com os seguintes valores:" + this);
    }

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
     * Nome da exchange. Padrão é 'message.dispatcher.ex'
     */
    private String exchangeName = "message.dispatcher.ex";

    /**
     * Tipo da exchange. Padrão é 'topic'
     */
    private Exchange exchangeType = MessageDispatcherConstants.Exchange.TOPIC;

    /**
     * Argumentos da exchange obrigatório quando o type = {@code Types.exchange.CONSISTENT_HASH} . Padrão é null
     */
    private Map<String, Object> exchangeConsistentHashArguments;

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
     * Tipo da exchange de dead letter. Padrão é 'topic'
     */
    private Exchange deadLetterExchangeType = MessageDispatcherConstants.Exchange.TOPIC;

    /**
     * Argumentos da exchange de dead letter obrigatório quando o type = {@code Types.exchange.CONSISTENT_HASH} . Padrão é null
     */
    private Map<String, Object> deadLetterExchangeConsistentHashArguments;

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
     * Intervalo inicial entre as tentativas. Padrão é 5000ms
     */
    private int initialInterval = 2000;

    /**
     * Multiplicador do intervalo entre as tentativas. Padrão é 2
     */
    private int multiplier = 2;

    /**
     * Intervalo máximo entre as tentativas. Padrão é 10000ms
     */
    private int maxInterval = 10000;

    /**
     * Quantidade de mensagens que serão consumidas por vez. Padrão é 10
     */
    @Min(1)
    @Max(100)
    private int prefetchCount = 10;

    /**
     * Tempo máximo de espera por uma resposta. Padrão é 10 segundos
     */
    private long replyTimeOut = 15_000;

    private Mapped mapped = new Mapped();

    /**
     * Indica se exceções ocorridas durante o processamento das mensagens dem ser retornadas ao cliente publicador.
     */
    private boolean returnExceptions = true;

    /**
     * Indica se o listener padrão deve ser ativado. Padrão é true
     */
    private boolean defaultListenerEnabled = true;

    public boolean isDefaultListenerEnabled() {
        return defaultListenerEnabled;
    }

    public void setDefaultListenerEnabled(boolean defaultListenerEnabled) {
        this.defaultListenerEnabled = defaultListenerEnabled;
    }

    public long getReplyTimeOut() {
        return replyTimeOut;
    }

    public void setReplyTimeOut(long replyTimeOut) {
        this.replyTimeOut = replyTimeOut;
    }

    public String getQueueName() {
        return queueName;
    }

    public void setQueueName(String queueName) {
        this.queueName = queueName.trim();
    }

    public String getExchangeName() {
        return exchangeName;
    }

    public void setExchangeName(String exchangeName) {
        this.exchangeName = exchangeName.trim();
    }

    public Exchange getExchangeType() {
        return exchangeType;
    }

    public void setExchangeType(Exchange exchangeType) {
        this.exchangeType = exchangeType;
    }

    public String getRoutingKey() {
        return routingKey;
    }

    public void setRoutingKey(String routingKey) {
        this.routingKey = routingKey.trim();
    }

    public String getConcurrency() {
        return concurrency;
    }

    public void setConcurrency(String concurrency) {
        this.concurrency = concurrency.trim();
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
        this.host = host.trim();
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
        this.username = username.trim();
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password.trim();
    }

    public String getVirtualHost() {
        return virtualHost;
    }

    public void setVirtualHost(String virtualHost) {
        this.virtualHost = virtualHost.trim();
    }

    public String getDeadLetterQueueName() {
        return deadLetterQueueName;
    }

    public void setDeadLetterQueueName(String deadLetterQueueName) {
        this.deadLetterQueueName = deadLetterQueueName.trim();
    }

    public String getDeadLetterExchangeName() {
        return deadLetterExchangeName;
    }

    public void setDeadLetterExchangeName(String deadLetterExchangeName) {
        this.deadLetterExchangeName = deadLetterExchangeName.trim();
    }

    public String getDeadLetterRoutingKey() {
        return deadLetterRoutingKey;
    }

    public void setDeadLetterRoutingKey(String deadLetterRoutingKey) {
        this.deadLetterRoutingKey = deadLetterRoutingKey.trim();
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

    public int getPrefetchCount() {
        return prefetchCount;
    }

    public void setPrefetchCount(int prefetchCount) {
        this.prefetchCount = prefetchCount;
    }

    public String[] getMappedHeaders() {
        return mapped.getMappedHeadersArray();
    }

    public boolean isReturnExceptions() {
        return returnExceptions;
    }

    public void setReturnExceptions(boolean returnExceptions) {
        this.returnExceptions = returnExceptions;
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
                ", prefetchCount=" + prefetchCount +
                ", replyTimeOut= " + replyTimeOut +
                ", mappedHeaders=" + mapped +
                '}';
    }

    public Mapped getMapped() {
        return mapped;
    }

    public void setMapped(Mapped mapped) {
        this.mapped = mapped;
    }

    public int minConsumers() {
        return Integer.parseInt(getConcurrency().split("-")[0]);
    }

    public int maxConsumers() {
        return Integer.parseInt(getConcurrency().split("-")[1]);
    }

    public Exchange getDeadLetterExchangeType() {
        return deadLetterExchangeType;
    }

    public void setDeadLetterExchangeType(Exchange deadLetterExchangeType) {
        this.deadLetterExchangeType = deadLetterExchangeType;
    }

    public Map<String, Object> getExchangeConsistentHashArguments() {
        return exchangeConsistentHashArguments;
    }

    public void setExchangeConsistentHashArguments(Map<String, Object> exchangeConsistentHashArguments) {
        this.exchangeConsistentHashArguments = exchangeConsistentHashArguments;
    }

    public Map<String, Object> getDeadLetterExchangeConsistentHashArguments() {
        return deadLetterExchangeConsistentHashArguments;
    }

    public void setDeadLetterExchangeConsistentHashArguments(Map<String, Object> deadLetterExchangeConsistentHashArguments) {
        this.deadLetterExchangeConsistentHashArguments = deadLetterExchangeConsistentHashArguments;
    }

    /**
     * Mapeia os headers que serão mapeados para o objeto MessageProperties do RabbitMQ.
     * <p>
     * Maps the headers that will be mapped to the MessageProperties of the RabbitMQ.
     * <p>
     * Os headers mapeados serão injetados em todas as mensagens enviadas ao broker pelo produtor
     * e no consumidor serão automaticamente injetadas em um {@link ThreadLocal} para serem utilizadas
     * em qualquer ponto da aplicação.
     * <p>
     * The mapped headers will be injected into all messages sent to the broker by the producer
     * and will be automatically injected into a {@link ThreadLocal} for use in any part of the application.
     */
    public static class Mapped {

        private String headers;

        private String[] mappedHeadersArray;

        public String getHeaders() {
            return headers;
        }

        public void setHeaders(String headers) {
            this.headers = headers;
        }

        public String[] getMappedHeadersArray() {
            if (mappedHeadersArray == null && headers != null) {
                mappedHeadersArray = Arrays.stream(headers.split(","))
                        .filter(s -> !s.isEmpty())
                        .map(String::trim)
                        .toArray(String[]::new);
            }
            return mappedHeadersArray;
        }

        public void setMappedHeadersArray(String[] mappedHeadersArray) {
            this.mappedHeadersArray = mappedHeadersArray;
        }

        @Override
        public String toString() {
            return "Mapped{" +
                    "headers=" + Arrays.toString(mappedHeadersArray) + '}';
        }
    }
}
