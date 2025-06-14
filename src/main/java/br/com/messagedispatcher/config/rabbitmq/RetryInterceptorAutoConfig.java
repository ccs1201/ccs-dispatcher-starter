package br.com.messagedispatcher.config.rabbitmq;

import br.com.messagedispatcher.config.properties.MessageDispatcherProperties;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.config.RetryInterceptorBuilder;
import org.springframework.amqp.rabbit.retry.MessageRecoverer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.interceptor.RetryOperationsInterceptor;

@Configuration
@ConditionalOnProperty(value = "message.dispatcher.default-listener-enabled", havingValue = "true", matchIfMissing = true)
public class RetryInterceptorAutoConfig {


    private static final Logger log = LoggerFactory.getLogger(RetryInterceptorAutoConfig.class);

    @PostConstruct
    public void init() {
        log.debug("Configurando RetryInterceptor");
    }

    @Bean
    protected RetryOperationsInterceptor retryOperationsInterceptor(MessageRecoverer messageRecoverer,
                                                                    MessageDispatcherProperties properties) {
        return RetryInterceptorBuilder.stateless()
                .maxAttempts(properties.getMaxRetryAttempts())
                .backOffOptions(
                        properties.getInitialInterval(),
                        properties.getMultiplier(),
                        properties.getMaxInterval()
                )
                .recoverer(messageRecoverer).build();
    }
}
