package br.com.ccs.messagedispatcher.router.impl;

import br.com.ccs.messagedispatcher.messaging.annotation.MessageHandler;
import br.com.ccs.messagedispatcher.messaging.annotation.MessageListener;
import br.com.ccs.messagedispatcher.exceptions.MessageHandlerNotFoundException;
import br.com.ccs.messagedispatcher.exceptions.MessageRouterMessageProcessException;
import br.com.ccs.messagedispatcher.exceptions.MessageRouterMissingHeaderException;
import br.com.ccs.messagedispatcher.router.Endpoint;
import br.com.ccs.messagedispatcher.router.MessageRouter;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import static br.com.ccs.messagedispatcher.messaging.publisher.MessageHeaders.HEADER_TYPE_ID;
import static org.apache.commons.lang3.StringUtils.isEmpty;

@Component
@ConditionalOnProperty(value = "message.dispatcher.router", havingValue = "annotated", matchIfMissing = true)
public class AnnotateddMessageRouter implements MessageRouter {

    private static final Logger log = LoggerFactory.getLogger(AnnotateddMessageRouter.class);

    private final ObjectMapper objectMapper;
    private final Map<String, Endpoint> handlersMap;

    public AnnotateddMessageRouter(ObjectMapper objectMapper, List<Endpoint> endpoints) {
        this.objectMapper = objectMapper;
        this.handlersMap = getHandlersMap(endpoints);
    }

    private static Map<String, Endpoint> getHandlersMap(final List<Endpoint> endpoints) {
        return endpoints.stream()
                .collect(Collectors
                        .toMap(AnnotateddMessageRouter::getDeclaredForClass, Function.identity()));
    }

    private static String getDeclaredForClass(final Endpoint endpoint) {

        if (endpoint.getClass().isAnnotationPresent(MessageListener.class)) {
            log.info("Found handler for type: {}", endpoint.getClass().getName());
            return "";
//                    endpoint.getClass()
//                    .getDeclaredAnnotation(MessageListener.class)
//                    .forClass()
//                    .getSimpleName();
        }
        throw new MessageHandlerNotFoundException(endpoint.getClass().getName() + " is not annotated with @MessageListener");
    }

    @Override
    public Object routeMessage(Message message) {
        var typeId = message.getMessageProperties().getHeaders().get(HEADER_TYPE_ID).toString();

        if (isEmpty(typeId)) {
            throw new MessageRouterMissingHeaderException("Missing " + HEADER_TYPE_ID + " header in the message");
        }

        var endpoint = Optional.ofNullable(handlersMap.get(typeId));

        if (endpoint.isEmpty()) {
            log.debug("No handler found for type: {}", typeId);
            throw new MessageHandlerNotFoundException("No handler found for type: " + typeId);
        }

        var parameterType = endpoint.getClass().getAnnotation(MessageHandler.class).forClass();

        try {
            var payload = objectMapper.readValue(message.getBody(), parameterType);
            return endpoint.get().handle(payload);
        } catch (IOException | RuntimeException e) {
            log.debug("Error processing message {}", e.getMessage());
            throw new MessageRouterMessageProcessException("Error processing message", e);
        }
    }
}

