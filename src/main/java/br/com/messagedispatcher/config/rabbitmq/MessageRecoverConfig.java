package br.com.messagedispatcher.config.rabbitmq;

import br.com.messagedispatcher.config.properties.MessageDispatcherProperties;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.retry.MessageRecoverer;
import org.springframework.amqp.rabbit.retry.RepublishMessageRecoverer;
import org.springframework.amqp.rabbit.support.ListenerExecutionFailedException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;
import java.util.Map;

import static br.com.messagedispatcher.publisher.MessageDispatcherHeaders.EXCEPTION_MESSAGE;
import static br.com.messagedispatcher.publisher.MessageDispatcherHeaders.EXCEPTION_TYPE;
import static br.com.messagedispatcher.publisher.MessageDispatcherHeaders.FAILED_AT;
import static br.com.messagedispatcher.publisher.MessageDispatcherHeaders.HAS_ERROR;

@Configuration
public class MessageRecoverConfig {

    private final Logger log = LoggerFactory.getLogger(MessageRecoverConfig.class);

    @Bean
    protected MessageRecoverer messageRecoverer(RabbitTemplate rabbitTemplate, MessageDispatcherProperties properties) {
        return new RepublishMessageRecoverer(rabbitTemplate,
                properties.getDeadLetterExchangeName(),
                properties.getDeadLetterRoutingKey()) {
            @Override
            protected Map<String, Object> additionalHeaders(Message message, Throwable cause) {
                Throwable rootCause = cause;

                if (cause instanceof ListenerExecutionFailedException e) {
                    rootCause = ExceptionUtils.getRootCause(e);
                }

                if (log.isDebugEnabled()) {
                    log.debug("Enviando mensagem para dead letter queue.", rootCause);
                }

                Map<String, Object> headers = message.getMessageProperties().getHeaders();
                headers.put(HAS_ERROR, true);
                headers.put(EXCEPTION_TYPE, rootCause.getClass().getSimpleName());
                headers.put(EXCEPTION_MESSAGE, rootCause.getMessage());
                headers.put(FAILED_AT, LocalDateTime.now());
                return headers;
            }
        };
    }
}
