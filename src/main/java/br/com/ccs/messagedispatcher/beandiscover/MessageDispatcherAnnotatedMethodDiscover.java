package br.com.ccs.messagedispatcher.beandiscover;

import br.com.ccs.messagedispatcher.messaging.MessageKinda;

import java.lang.reflect.Method;

public interface MessageDispatcherAnnotatedMethodDiscover {
    Method getHandler(MessageKinda actionType, String parameterType);
}
