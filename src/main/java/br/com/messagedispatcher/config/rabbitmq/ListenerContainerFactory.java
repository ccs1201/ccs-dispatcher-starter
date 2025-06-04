package br.com.messagedispatcher.config.rabbitmq;

import br.com.messagedispatcher.config.properties.MessageDispatcherProperties;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.interceptor.RetryOperationsInterceptor;

import java.util.concurrent.Executors;


/**
 * @author Cleber Souza
 * @version 1.0
 */
@Configuration
public class ListenerContainerFactory {

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
        factory.setConsumerTagStrategy(queue -> queue + "-consumer");

        //configura o número de mensagens que serão consumidas de uma vez
        factory.setPrefetchCount(properties.getPrefetchCount());

        var minConsumers = properties.minConsumers();
        var maxConsumers = properties.maxConsumers();

        //configura a concorrência de consumidores
        factory.setConcurrentConsumers(minConsumers);
        factory.setMaxConcurrentConsumers(maxConsumers);

        factory.setTaskExecutor(Executors.newFixedThreadPool(maxConsumers, Thread.ofVirtual().factory()));
        return factory;
    }
}
