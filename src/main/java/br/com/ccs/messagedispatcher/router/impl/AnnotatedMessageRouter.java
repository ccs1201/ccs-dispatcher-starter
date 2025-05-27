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
import org.springframework.beans.BeanUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.test.util.ApplicationContextTestUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;

import static br.com.ccs.messagedispatcher.messaging.publisher.MessageDispatcherHeaders.HEADER_MESSAGE_ACTION;
import static br.com.ccs.messagedispatcher.messaging.publisher.MessageDispatcherHeaders.HEADER_TYPE_ID;
import static org.apache.commons.lang3.StringUtils.isEmpty;

@Component
@ConditionalOnProperty(value = "message.dispatcher.router", havingValue = "annotated", matchIfMissing = true)
public class AnnotatedMessageRouter implements MessageRouter {

    private static final Logger log = LoggerFactory.getLogger(AnnotatedMessageRouter.class);

    private final ObjectMapper objectMapper;
    private final MessageDispatcherAnnotatedMethodDiscover annotatedMethodDiscover;

    public AnnotatedMessageRouter(ObjectMapper objectMapper, MessageDispatcherAnnotatedMethodDiscover annotatedMethodDiscover) {
        this.objectMapper = objectMapper;
        this.annotatedMethodDiscover = annotatedMethodDiscover;
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
                            .valueOf(message.getMessageProperties().getHeader(HEADER_MESSAGE_ACTION)),
                    typeId);

            var payload = objectMapper.readValue(message.getBody(), handler.getParameterTypes()[0]);
            return handler.invoke(annotatedMethodDiscover.getApplicationContext().getBean(handler.getDeclaringClass()), payload);
        } catch (Exception e) {
            log.error("Error processando mensagem: {}", e.getMessage(), e);
            throw new MessageRouterMessageProcessException("Erro ao processar mensagem. Detail: " + e.getMessage(), e);
        }
    }
}

