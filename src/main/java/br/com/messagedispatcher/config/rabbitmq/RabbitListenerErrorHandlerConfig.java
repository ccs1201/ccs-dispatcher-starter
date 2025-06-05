package br.com.messagedispatcher.config.rabbitmq;

import br.com.messagedispatcher.exceptions.MessageDispatcherRetryableException;
import br.com.messagedispatcher.model.HandlerType;
import br.com.messagedispatcher.model.MessageDispatcherRemoteInvocationResult;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.listener.api.RabbitListenerErrorHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

import static br.com.messagedispatcher.constants.MessageDispatcherConstants.MessageDispatcherHeaders;
import static java.util.Objects.nonNull;
import static java.util.Objects.requireNonNull;
import static org.apache.commons.lang3.exception.ExceptionUtils.getRootCause;

@Configuration
public class RabbitListenerErrorHandlerConfig {

    private static final Logger log = LoggerFactory.getLogger(RabbitListenerErrorHandlerConfig.class);
    private final List<String> retryableMessageTypes = List.of(HandlerType.QUERY.name(), HandlerType.COMMAND.name());

    @Bean
    public RabbitListenerErrorHandler messageDispatcherErrorHandler() {
        return (amqpMessage, channel, message, exception) -> {
            String handlerType = (String) amqpMessage.getMessageProperties().getHeaders().get(MessageDispatcherHeaders.HANDLER_TYPE_HEADER);

            if (nonNull(handlerType) && retryableMessageTypes.contains(handlerType) && shouldReply(amqpMessage)) {
                return MessageDispatcherRemoteInvocationResult.of(getRootCause(exception));
            } else {
                log.error("Erro processando mensagem do tipo: {}", handlerType, exception);
                throw new MessageDispatcherRetryableException("Erro ao processar mensagem " + getRootCause(exception).getMessage(),
                        exception);
            }
        };
    }

    private boolean shouldReply(Message message) {
        requireNonNull(message, "A mensagem não deveria ser null");
        return StringUtils.isNotEmpty(message.getMessageProperties().getReplyTo());
    }
}
