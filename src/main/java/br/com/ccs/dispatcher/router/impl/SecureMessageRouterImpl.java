package br.com.ccs.dispatcher.router.impl;

import br.com.ccs.dispatcher.router.MessageRouter;
import org.springframework.amqp.core.Message;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(value = "dispatcher.security.enabled", havingValue = "true")
public class SecureMessageRouterImpl implements MessageRouter {
    @Override
    public Object handleMessage(Message message) {
        throw new UnsupportedOperationException("Not implemented yet");
    }
}
