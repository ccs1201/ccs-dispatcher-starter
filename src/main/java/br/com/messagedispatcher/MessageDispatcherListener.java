package br.com.messagedispatcher;

import org.springframework.amqp.core.Message;

public interface MessageDispatcherListener {
    Object onMessage(Message message);
}
