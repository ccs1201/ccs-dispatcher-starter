package br.com.messagedispatcher.listener;

import org.springframework.amqp.core.Message;

public interface MessageDispatcherListener {
    Object onMessage(Message message);
}
