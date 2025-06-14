package br.com.messagedispatcher.config.rabbitmq;

import br.com.messagedispatcher.config.properties.MessageDispatcherProperties;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.interceptor.RetryOperationsInterceptor;


/**
 * @author Cleber Souza
 * @version 1.0
 */
@Configuration
@ConditionalOnProperty(value = "message.dispatcher.default-listener-enabled", havingValue = "true", matchIfMissing = true)
public class ListenerContainerFactoryAutoConfig {

    private static final Logger log = LoggerFactory.getLogger(ListenerContainerFactoryAutoConfig.class);

    @PostConstruct
    public void init() {
        log.debug("Configurando SimpleRabbitListenerContainerFactory");
    }

    @Bean
    protected SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(ConnectionFactory connectionFactory,
                                                                                  MessageConverter messageConverter,
                                                                                  RetryOperationsInterceptor retryOperationsInterceptor,
                                                                                  MessageDispatcherProperties properties) {
        var minConsumers = properties.minConsumers();
        var maxConsumers = properties.maxConsumers();

        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(messageConverter);
        factory.setDefaultRequeueRejected(false);
        factory.setAdviceChain(retryOperationsInterceptor);
        factory.setConsumerTagStrategy(queue -> queue + "-consumer");
        factory.setPrefetchCount(properties.getPrefetchCount());
        factory.setConcurrentConsumers(minConsumers);
        factory.setMaxConcurrentConsumers(maxConsumers);

        log.debug("RabbitListenerContainerFactory configurado: {}", factory);

        return factory;
    }
}
