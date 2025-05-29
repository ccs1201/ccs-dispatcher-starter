package br.com.ccs.messagedispatcher.beandiscover;

import br.com.ccs.messagedispatcher.messaging.MessageAction;

import java.lang.reflect.Method;

public interface MessageDispatcherAnnotatedMethodDiscover {
    Method getHandler(MessageAction actionType,String parameterType);
}
