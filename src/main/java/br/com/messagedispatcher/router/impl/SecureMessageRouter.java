package br.com.messagedispatcher.router.impl;

import br.com.messagedispatcher.router.MessageRouter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(value = "message.dispatcher.router", havingValue = "secure")
public class SecureMessageRouter implements MessageRouter {
    @Override
    public String routeMessage(Object objectMessage) {
        throw new UnsupportedOperationException("Not implemented yet");
    }
}
