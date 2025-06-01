package br.com.ccs.messagedispatcher.beandiscover;

import br.com.ccs.messagedispatcher.messaging.MessageType;

import java.lang.reflect.Method;

public interface MessageDispatcherAnnotatedMethodDiscover {
    Method getHandler(MessageType actionType, String parameterType);
}
