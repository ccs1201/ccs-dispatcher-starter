package br.com.messagedispatcher.config.rabbitmq;

import br.com.messagedispatcher.config.properties.MessageDispatcherProperties;
import org.springframework.amqp.rabbit.config.RetryInterceptorBuilder;
import org.springframework.amqp.rabbit.retry.MessageRecoverer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.interceptor.RetryOperationsInterceptor;

@Configuration
public class RetryInterceptorConfig {

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
