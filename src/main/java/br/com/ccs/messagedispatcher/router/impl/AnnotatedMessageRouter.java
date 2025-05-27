package br.com.ccs.messagedispatcher.router.impl;

import br.com.ccs.messagedispatcher.beandiscover.MessageDispatcherAnnotatedMethodDiscover;
import br.com.ccs.messagedispatcher.exceptions.MessageRouterMessageProcessException;
import br.com.ccs.messagedispatcher.exceptions.MessageRouterMissingHeaderException;
import br.com.ccs.messagedispatcher.messaging.MessageAction;
import br.com.ccs.messagedispatcher.router.MessageRouter;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationTargetException;

import static br.com.ccs.messagedispatcher.messaging.publisher.MessageDispatcherHeaders.HEADER_MESSAGE_ACTION;
import static br.com.ccs.messagedispatcher.messaging.publisher.MessageDispatcherHeaders.HEADER_TYPE_ID;
import static org.apache.commons.lang3.StringUtils.isEmpty;

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
        var typeId = message.getMessageProperties().getHeaders().get(HEADER_TYPE_ID).toString();

        if (isEmpty(typeId)) {
            throw new MessageRouterMissingHeaderException("Missing " + HEADER_TYPE_ID + " header in the message");
        }

        try {
            var handler = annotatedMethodDiscover.getHandler(MessageAction
                    .valueOf(message.getMessageProperties().getHeader(HEADER_MESSAGE_ACTION)), typeId);

            var payload = objectMapper.readValue(message.getBody(), handler.getParameterTypes()[0]);

            return handler.invoke(applicationContext.getBean(handler.getDeclaringClass()), payload);

        } catch (InvocationTargetException e) {
            throw new MessageRouterMessageProcessException("Erro ao processar mensagem. Detail: " + e.getTargetException().getMessage(), e.getTargetException());
        } catch (Exception e) {
            throw new MessageRouterMessageProcessException("Erro ao processar mensagem. Detail: " + e.getMessage(), e);
        }
    }
}

