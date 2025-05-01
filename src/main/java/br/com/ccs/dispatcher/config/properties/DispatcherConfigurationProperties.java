package br.com.ccs.dispatcher.config.properties;

import br.com.ccs.dispatcher.config.CcsDispatcherAutoConfiguration;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import java.util.logging.Logger;

@AutoConfigureBefore(CcsDispatcherAutoConfiguration.class)
@Component("ccsDispatcherProperties")
@ConfigurationProperties(prefix = "ccs.dispatcher")
@Validated
public class DispatcherConfigurationProperties {

    @PostConstruct
    public void init() {
        final Logger log = Logger.getLogger(DispatcherConfigurationProperties.class.getName());
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

        log.info("DispatcherProperties inicializado com os seguintes valores:\n" + this);
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
                '}';
    }
}
