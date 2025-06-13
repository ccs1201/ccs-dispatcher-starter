package br.com.messagedispatcher.router.impl;

import br.com.messagedispatcher.beandiscover.MessageDispatcherAnnotatedHandlerDiscover;
import br.com.messagedispatcher.exceptions.MessageRouterMissingHeaderException;
import br.com.messagedispatcher.router.MessageRouter;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.core.Message;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationTargetException;
import java.util.Optional;

import static br.com.messagedispatcher.constants.MessageDispatcherConstants.HandlerType.valueOf;
import static br.com.messagedispatcher.constants.MessageDispatcherConstants.Headers.BODY_TYPE;
import static br.com.messagedispatcher.constants.MessageDispatcherConstants.Headers.HANDLER_TYPE;
import static java.util.Objects.isNull;

@Component
@ConditionalOnProperty(value = "message.dispatcher.router", havingValue = "annotated", matchIfMissing = true)
public class AnnotatedMessageRouter implements MessageRouter {

    private final ObjectMapper objectMapper;
    private final MessageDispatcherAnnotatedHandlerDiscover annotatedMethodDiscover;
    private final ApplicationContext applicationContext;

    public AnnotatedMessageRouter(ObjectMapper objectMapper, MessageDispatcherAnnotatedHandlerDiscover annotatedMethodDiscover,
                                  ApplicationContext applicationContext) {
        this.objectMapper = objectMapper;
        this.annotatedMethodDiscover = annotatedMethodDiscover;
        this.applicationContext = applicationContext;
    }

    @Override
    public Object routeMessage(Object objectMessage) {
        var message = (Message) objectMessage;
        var bodyType = message.getMessageProperties().getHeaders().get(BODY_TYPE.getHeaderName());
        var handlerType = Optional.ofNullable(message.getMessageProperties().getHeaders().get(HANDLER_TYPE.getHeaderName()));

        if (isNull(bodyType)) {
            handleHeaderError(BODY_TYPE.getHeaderName());
        }

        if (handlerType.isEmpty()) {
            handleHeaderError(HANDLER_TYPE.getHeaderName());
        }

        try {
            var handlerMethod = annotatedMethodDiscover.getHandler(valueOf(handlerType.get().toString()), bodyType.toString());

            var payload = objectMapper.readValue(message.getBody(), handlerMethod.getParameterTypes()[0]);

            return handlerMethod.invoke(applicationContext.getBean(handlerMethod.getDeclaringClass()), payload);

        } catch (InvocationTargetException e) {
            throw new RuntimeException(e.getTargetException());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void handleHeaderError(String header) {
        throw new MessageRouterMissingHeaderException("Header " + header + " ausente na mensagem.");
    }
}

