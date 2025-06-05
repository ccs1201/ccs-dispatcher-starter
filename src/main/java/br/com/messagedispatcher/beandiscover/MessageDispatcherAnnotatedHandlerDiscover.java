package br.com.messagedispatcher.beandiscover;

import br.com.messagedispatcher.model.HandlerType;

import java.lang.reflect.Method;

public interface MessageDispatcherAnnotatedHandlerDiscover {
    Method getHandler(HandlerType actionType, String parameterType);
}
