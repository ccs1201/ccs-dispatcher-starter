package br.com.ccs.dispatcher.router.impl;

import br.com.ccs.dispatcher.messaging.annotation.MessageListener;
import br.com.ccs.dispatcher.messaging.exceptions.MessageRouterException;
import br.com.ccs.dispatcher.router.Endpoint;
import br.com.ccs.dispatcher.router.MessageRouter;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.apache.commons.lang3.StringUtils.isEmpty;

@Component
@Primary
public class AnnotateddMessageRouter implements MessageRouter {

    private final Logger log = LoggerFactory.getLogger(AnnotateddMessageRouter.class);

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
            return endpoint.getClass()
                    .getDeclaredAnnotation(MessageListener.class)
                    .forClass()
                    .getSimpleName();
        }
        throw new MessageRouterException(endpoint.getClass().getName() + " is not annotated with @MessageListener", null);
    }

    @Override
    public Object handleMessage(Message message) {
        var typeId = message.getMessageProperties().getHeaders().get("__TypeId__").toString();


        if (isEmpty(typeId)) {
            log.warn("Missing __TypeId__ header in the message");
            throw new MessageRouterException("Missing __TypeId__ header in the message", null);
        }

        final var type = typeId.substring(typeId.lastIndexOf(".") + 1);

        var endpoint = Optional.ofNullable(handlersMap.get(type));
        try {
            if (endpoint.isEmpty()) {
                log.warn("No handler found for type: {}", typeId);
                return null;
            }

            var parameterType = endpoint.getClass().getAnnotation(MessageListener.class).forClass();
           
            var payload = objectMapper.readValue(message.getBody(), parameterType);

            return endpoint.get().handle(payload);

        } catch (Exception e) {
            log.error("Handler #{} failed to process message: {}", endpoint.getClass().getName(), e.getMessage(), e);
            throw new MessageRouterException("Fail processing message: " + e.getMessage(), e);
        }
    }
}

