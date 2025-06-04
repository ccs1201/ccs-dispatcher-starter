package br.com.messagedispatcher.config.rabbitmq;

import br.com.messagedispatcher.exceptions.MessageDispatcherRetryableException;
import br.com.messagedispatcher.model.MessageDispatcherRemoteInvocationResult;
import br.com.messagedispatcher.model.MessageType;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.listener.api.RabbitListenerErrorHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Objects;

import static br.com.messagedispatcher.constants.MessageDispatcherConstants.MessageDispatcherHeaders;
import static br.com.messagedispatcher.constants.MessageDispatcherConstants.REPLY_TO_HEADER;
import static org.apache.commons.lang3.exception.ExceptionUtils.getRootCause;

@Configuration
public class RabbitListenerErrorHandlerConfig {

    private final List<String> retryableMessageTypes = List.of(MessageType.QUERY.name(), MessageType.COMMAND.name());

    @Bean
    public RabbitListenerErrorHandler messageDispatcherErrorHandler() {
        return (amqpMessage, channel, message, exception) -> {
            String messageType = (String) amqpMessage.getMessageProperties().getHeaders().get(MessageDispatcherHeaders.MESSAGE_TYPE);


            if (retryableMessageTypes.contains(messageType) && shouldReply(amqpMessage)) {
                return MessageDispatcherRemoteInvocationResult.of(getRootCause(exception));
            } else {
                throw new MessageDispatcherRetryableException("Erro processando mensagem do tipo: " + messageType,
                        getRootCause(exception));
            }
        };
    }

    private boolean shouldReply(Message message) {
        Objects.requireNonNull(message, "A mensagem não deveria ser null");
        return message.getMessageProperties().getHeaders().containsKey(REPLY_TO_HEADER);
    }
}
