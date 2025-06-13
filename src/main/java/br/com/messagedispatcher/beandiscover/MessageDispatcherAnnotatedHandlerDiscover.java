package br.com.messagedispatcher.beandiscover;

import java.lang.reflect.Method;

import static br.com.messagedispatcher.constants.MessageDispatcherConstants.HandlerType;

public interface MessageDispatcherAnnotatedHandlerDiscover {
    Method getHandler(HandlerType actionType, String parameterType);
}
