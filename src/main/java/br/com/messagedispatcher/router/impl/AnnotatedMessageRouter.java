package br.com.messagedispatcher.router.impl;

import br.com.messagedispatcher.beandiscover.MessageDispatcherAnnotatedHandlerDiscover;
import br.com.messagedispatcher.exceptions.MessageRouterMissingHeaderException;
import br.com.messagedispatcher.model.HandlerType;
import br.com.messagedispatcher.router.MessageRouter;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.core.Message;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationTargetException;

import static br.com.messagedispatcher.constants.MessageDispatcherConstants.MessageDispatcherHeaders.BODY_TYPE_HEADER;
import static br.com.messagedispatcher.constants.MessageDispatcherConstants.MessageDispatcherHeaders.HANDLER_TYPE_HEADER;
import static org.apache.commons.lang3.StringUtils.isEmpty;

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
        var bodyType = message.getMessageProperties().getHeaders().get(BODY_TYPE_HEADER).toString();

        if (isEmpty(bodyType)) {
            handleHeaderError(BODY_TYPE_HEADER);
        }

        if (isEmpty(message.getMessageProperties().getHeader(HANDLER_TYPE_HEADER))) {
            handleHeaderError(HANDLER_TYPE_HEADER);
        }

        try {
            var handler = annotatedMethodDiscover.getHandler(HandlerType
                    .valueOf(message.getMessageProperties().getHeader(HANDLER_TYPE_HEADER)), bodyType);

            var payload = objectMapper.readValue(message.getBody(), handler.getParameterTypes()[0]);

            return handler.invoke(applicationContext.getBean(handler.getDeclaringClass()), payload);

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

