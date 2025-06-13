package br.com.messagedispatcher.config;

import br.com.messagedispatcher.config.properties.MessageDispatcherProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ConnectionFactoryAutoConfig {

    private final Logger log = LoggerFactory.getLogger(ConnectionFactoryAutoConfig.class);

    @Bean
    @SuppressWarnings("unused")
    protected ConnectionFactory connectionFactory(final MessageDispatcherProperties properties) {
        log.debug("Configurando ConnectionFactory");
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory();
        connectionFactory.setHost(properties.getHost());
        connectionFactory.setPort(properties.getPort());
        connectionFactory.setUsername(properties.getUsername());
        connectionFactory.setPassword(properties.getPassword());
        connectionFactory.setVirtualHost(properties.getVirtualHost());
        connectionFactory.setPublisherConfirmType(CachingConnectionFactory.ConfirmType.CORRELATED);
        connectionFactory.setPublisherReturns(true);

        log.debug("ConnectionFactory configurada");
        return connectionFactory;
    }
}
