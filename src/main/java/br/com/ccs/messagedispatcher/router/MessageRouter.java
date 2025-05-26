package br.com.ccs.messagedispatcher.router;

import org.springframework.amqp.core.Message;

public interface MessageRouter {
    Object routeMessage(Object objectMessage);
}
