package br.com.ccs.dispatcher.router;

import org.springframework.amqp.core.Message;

public interface MessageRouter {
    Object handleMessage(Message message);
}
