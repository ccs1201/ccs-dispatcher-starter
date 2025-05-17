package br.com.ccs.dispatcher.router.impl;

import br.com.ccs.dispatcher.messaging.annotation.EndpointImpl;
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

import static java.util.Objects.nonNull;

@Component
@Primary
public class AnnotedMessageRouter implements MessageRouter {

    private final Logger log = LoggerFactory.getLogger(AnnotedMessageRouter.class);

    private final ObjectMapper objectMapper;
    private final Map<String, Endpoint> handlersMap;

    public AnnotedMessageRouter(ObjectMapper objectMapper, List<Endpoint> endpoints) {
        this.objectMapper = objectMapper;
        this.handlersMap = getHandlersMap(endpoints);
    }

    private static Map<String, Endpoint> getHandlersMap(final List<Endpoint> endpoints) {
        return endpoints.stream()
                .collect(Collectors
                        .toMap(AnnotedMessageRouter::getDeclaredForClass, Function.identity()));
    }

    private static String getDeclaredForClass(final Endpoint endpoint) {

        if (endpoint.getClass().isAnnotationPresent(EndpointImpl.class)) {
            return endpoint.getClass()
                    .getDeclaredAnnotation(EndpointImpl.class)
                    .forClass()
                    .getSimpleName();
        }
        throw new MessageRouterException(endpoint.getClass().getName() + " is not annotated with @HandlerImpl", null);
    }

    @Override
    public Object handleMessage(Message message) {
        var typeId = message.getMessageProperties().getHeaders().get("__TypeId__").toString();

        if (nonNull(typeId)) {

            final var type = typeId.substring(typeId.lastIndexOf(".") + 1);

            var endpointToCall = Optional.empty();
            try {
                endpointToCall = Optional.ofNullable(handlersMap.get(type));
                endpointToCall.ifPresentOrElse(endpoint -> {
                    try {
                        var parameterType = endpoint.getClass().getAnnotation(EndpointImpl.class).forClass();
                        final var payload = objectMapper.readValue(message.getBody(), parameterType);

                        // Casting necessário para o tipo genérico
                        @SuppressWarnings("unchecked")
                        Endpoint<Object, Object> typedEndpoint = (Endpoint<Object, Object>) endpoint;
                        typedEndpoint.handle(payload);

                    } catch (ClassCastException e) {
                        log.error("Handler found but failed to cast: {}", endpoint.getClass().getSimpleName(), e);
                        throw new MessageRouterException("Handler casting failed", e);
                    } catch (Exception e) {
                        log.error("Handler #{} failed to process message", endpoint.getClass().getSimpleName(), e);
                        throw new MessageRouterException("Fail processing message: " + e.getMessage(), e);
                    }
                }, () -> log.warn("No handler found for type: {}", typeId));

            } catch (Exception e) {
                log.error("Failed to process message with type: {}", typeId, e);
                throw new MessageRouterException("Failed to process message", e);
            }
        } else {
            log.warn("Missing __TypeId__ header in the message");
            throw new MessageRouterException("Missing __TypeId__ header in the message", null);
        }
        return null;
    }
}
