package br.com.ccs.messagedispatcher.config.rabbitmq;

import br.com.ccs.messagedispatcher.config.properties.MessageDispatcherProperties;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.interceptor.RetryOperationsInterceptor;

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

        //configura o número de mensagens que serão consumidas de uma vez
        factory.setPrefetchCount(properties.getPrefetchCount());
        //configura a concorrência de consumidores
        factory.setConcurrentConsumers(
                Integer.parseInt(properties.getConcurrency().split("-")[0]));
        factory.setMaxConcurrentConsumers(
                Integer.parseInt(properties.getConcurrency().split("-")[1]));

        return factory;
    }
}
