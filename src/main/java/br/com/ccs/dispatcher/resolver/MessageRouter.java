package br.com.ccs.dispatcher.resolver;

import org.springframework.amqp.core.Message;

public interface MessageRouter {
    Object handleMessage(Message message);
}
