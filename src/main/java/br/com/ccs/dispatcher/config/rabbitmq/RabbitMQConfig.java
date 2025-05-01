package br.com.ccs.dispatcher.config.rabbitmq;

import br.com.ccs.dispatcher.config.CcsDispatcherAutoConfiguration;
import br.com.ccs.dispatcher.config.properties.DispatcherConfigurationProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.annotation.PostConstruct;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

import java.text.DateFormat;
import java.util.logging.Logger;

@Configuration
@AutoConfigureAfter(CcsDispatcherAutoConfiguration.class)
@ConditionalOnProperty(name = "ccs.dispatcher.enabled", havingValue = "true", matchIfMissing = true)
public class RabbitMQConfig {

    private final Logger log = Logger.getLogger(RabbitMQConfig.class.getName());
    private final DispatcherConfigurationProperties properties;

    public RabbitMQConfig(DispatcherConfigurationProperties properties) {
        this.properties = properties;
    }

    @PostConstruct
    public void init() {
        if (properties.getQueueName() == null || properties.getQueueName().trim().isEmpty()) {
            throw new IllegalStateException("Queue name não pode ser null ou vazio");
        }
        log.info("Propriedades validadas com sucesso");
        log.info("Propriedades carregas: ".concat(properties.toString()));

        log.info("RabbitMQConfig inicializado.");
    }

    @Bean
    @Primary
    public ConnectionFactory connectionFactory() {
        log.info("Configurando ConnectionFactory");
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory();
        connectionFactory.setHost(properties.getHost());
        connectionFactory.setPort(properties.getPort());
        connectionFactory.setUsername(properties.getUsername());
        connectionFactory.setPassword(properties.getPassword());
        connectionFactory.setVirtualHost(properties.getVirtualHost());
        connectionFactory.setPublisherConfirmType(CachingConnectionFactory.ConfirmType.CORRELATED);
        connectionFactory.setPublisherReturns(true);

        log.info("ConnectionFactory configurada");
        return connectionFactory;
    }

    @Bean
    @Primary
    public RabbitAdmin rabbitAdmin(ConnectionFactory connectionFactory) {
        log.info("Configurando RabbitAdmin");
        return new RabbitAdmin(connectionFactory);
    }

    @Bean
    @Primary
    public Exchange dispatcherExchange() {
        log.info("Configurando exchange: " + properties.getExchangeName());
        var ex = ExchangeBuilder
                .topicExchange(properties.getExchangeName())
                .durable(properties.isExchangeDurable())
                .build();

        log.info("Exchange criada: " + ex);
        return ex;
    }

    @Bean
    public Exchange deadLetterExchange() {
        return ExchangeBuilder
                .topicExchange(properties.getDeadLetterExchangeName())
                .durable(true)
                .build();
    }

    @Bean
    @Primary
    public Queue dispatcherQueue() {
        log.info("Configurando queue: " + properties.getQueueName());

        return QueueBuilder
                .durable(properties.getQueueName())
                .deadLetterRoutingKey(properties.getDeadLetterRoutingKey())
                .deadLetterExchange(properties.getDeadLetterExchangeName())
                .build();
    }

    @Bean
    public Queue deadLetterQueue() {
        return QueueBuilder
                .durable(properties.getDeadLetterQueueName())
                .build();
    }

    @Bean
    @Primary
    public Binding dispatcherBinding(Queue ccsDispatcherQueue,
                                     Exchange ccsDispatcherExchange) {
        log.info("Configurando binding: " + properties.getRoutingKey());
        return BindingBuilder
                .bind(ccsDispatcherQueue)
                .to(ccsDispatcherExchange)
                .with(properties.getRoutingKey())
                .noargs();
    }

    @Bean
    @Primary
    public MessageConverter jackson2JsonMessageConverter() {
        log.info("Configurando Jackson2JsonMessageConverter");
        return new Jackson2JsonMessageConverter(
                Jackson2ObjectMapperBuilder
                        .json()
                        .dateFormat(DateFormat.getDateTimeInstance())
                        .build()
                        .setDefaultPropertyInclusion(JsonInclude.Include.NON_NULL));
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

    @Bean
    @Primary
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory, MessageConverter messageConverter) {
        log.info("Configurando RabbitTemplate");
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(messageConverter);

        // Configurando exchange e routing key padrão
        template.setExchange(properties.getExchangeName());
        template.setRoutingKey(properties.getRoutingKey());

        // Configurando confirmação de publicação
        template.setMandatory(true);
        template.setConfirmCallback((correlationData, ack, cause) -> {
            if (ack) {
                log.info("Mensagem confirmada: " + correlationData);
            } else {
                log.warning("Mensagem não confirmada: " + cause);
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
}
