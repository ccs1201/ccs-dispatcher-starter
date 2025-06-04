package br.com.messagedispatcher.router.impl;

import br.com.messagedispatcher.beandiscover.MessageDispatcherAnnotatedMethodDiscover;
import br.com.messagedispatcher.exceptions.MessageRouterMissingHeaderException;
import br.com.messagedispatcher.model.MessageType;
import br.com.messagedispatcher.router.MessageRouter;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationTargetException;

import static br.com.messagedispatcher.constants.MessageDispatcherConstants.MessageDispatcherHeaders.BODY_TYPE;
import static br.com.messagedispatcher.constants.MessageDispatcherConstants.MessageDispatcherHeaders.MESSAGE_TYPE;
import static org.apache.commons.lang3.StringUtils.isEmpty;

@SuppressWarnings("unused")
@Component
@ConditionalOnProperty(value = "message.dispatcher.router", havingValue = "annotated", matchIfMissing = true)
public class AnnotatedMessageRouter implements MessageRouter {

    private static final Logger log = LoggerFactory.getLogger(AnnotatedMessageRouter.class);

    private final ObjectMapper objectMapper;
    private final MessageDispatcherAnnotatedMethodDiscover annotatedMethodDiscover;
    private final ApplicationContext applicationContext;

    public AnnotatedMessageRouter(ObjectMapper objectMapper, MessageDispatcherAnnotatedMethodDiscover annotatedMethodDiscover,
                                  ApplicationContext applicationContext) {
        this.objectMapper = objectMapper;
        this.annotatedMethodDiscover = annotatedMethodDiscover;
        this.applicationContext = applicationContext;
    }

    @Override
    public Object routeMessage(Object objectMessage) {
        var message = (Message) objectMessage;
        var typeId = message.getMessageProperties().getHeaders().get(BODY_TYPE).toString();

        if (isEmpty(typeId)) {
            throw new MessageRouterMissingHeaderException("Missing " + BODY_TYPE + " header in the message");
        }

        try {
            var handler = annotatedMethodDiscover.getHandler(MessageType
                    .valueOf(message.getMessageProperties().getHeader(MESSAGE_TYPE)), typeId);

            var payload = objectMapper.readValue(message.getBody(), handler.getParameterTypes()[0]);

            return handler.invoke(applicationContext.getBean(handler.getDeclaringClass()), payload);

        } catch (InvocationTargetException e) {
            throw new RuntimeException(e.getTargetException());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

