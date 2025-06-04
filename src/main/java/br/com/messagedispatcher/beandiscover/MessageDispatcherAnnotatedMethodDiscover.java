package br.com.messagedispatcher.beandiscover;

import br.com.messagedispatcher.model.MessageType;

import java.lang.reflect.Method;

public interface MessageDispatcherAnnotatedMethodDiscover {
    Method getHandler(MessageType actionType, String parameterType);
}
