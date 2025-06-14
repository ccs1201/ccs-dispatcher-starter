package br.com.messagedispatcher.config.rabbitmq;

import br.com.messagedispatcher.exceptions.MessageDispatcherRetryableException;
import br.com.messagedispatcher.model.MessageDispatcherRemoteInvocationResult;
import jakarta.annotation.PostConstruct;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.listener.api.RabbitListenerErrorHandler;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

import static br.com.messagedispatcher.constants.MessageDispatcherConstants.HandlerType.COMMAND;
import static br.com.messagedispatcher.constants.MessageDispatcherConstants.HandlerType.QUERY;
import static br.com.messagedispatcher.constants.MessageDispatcherConstants.Headers;
import static java.util.Objects.nonNull;
import static java.util.Objects.requireNonNull;
import static org.apache.commons.lang3.exception.ExceptionUtils.getRootCause;

@Configuration
@ConditionalOnProperty(value = "message.dispatcher.default-listener-enabled", havingValue = "true", matchIfMissing = true)
public class RabbitListenerErrorHandlerAutoConfig {

    private static final Logger log = LoggerFactory.getLogger(RabbitListenerErrorHandlerAutoConfig.class);
    private final List<String> retryableMessageTypes = List.of(QUERY.name(), COMMAND.name());

    @PostConstruct
    public void init() {
        log.debug("Configurando RabbitListenerErrorHandler.");
    }

    @Bean
    public RabbitListenerErrorHandler messageDispatcherErrorHandler() {
        return (amqpMessage, channel, message, exception) -> {
            var handlerType = amqpMessage.getMessageProperties().getHeaders().get(Headers.HANDLER_TYPE.getHeaderName());

            incrementRetryCount(amqpMessage);

            if (nonNull(handlerType) && retryableMessageTypes.contains(handlerType.toString()) && shouldReply(amqpMessage)) {
                return MessageDispatcherRemoteInvocationResult.of(getRootCause(exception));
            } else {
                log.error("Erro processando mensagem do tipo: {}", handlerType, exception);
                throw new MessageDispatcherRetryableException(getRootCause(exception).getMessage(), exception);
            }
        };
    }

    private static void incrementRetryCount(Message amqpMessage) {
        var retryCount = amqpMessage.getMessageProperties().getHeaders().get(AmqpHeaders.RETRY_COUNT);

        if (nonNull(retryCount)) {
            var count = Integer.parseInt(retryCount.toString());
            amqpMessage.getMessageProperties().getHeaders().put(AmqpHeaders.RETRY_COUNT, count + 1);
        } else {
            amqpMessage.getMessageProperties().getHeaders().put(AmqpHeaders.RETRY_COUNT, 1);
        }
    }

    private boolean shouldReply(Message message) {
        requireNonNull(message, "A mensagem não deveria ser null");
        return StringUtils.isNotEmpty(message.getMessageProperties().getReplyTo());
    }
}
