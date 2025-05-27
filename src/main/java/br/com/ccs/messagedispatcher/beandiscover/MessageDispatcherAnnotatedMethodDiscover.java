package br.com.ccs.messagedispatcher.beandiscover;

import br.com.ccs.messagedispatcher.messaging.MessageAction;
import org.springframework.context.ApplicationContext;

import java.lang.reflect.Method;

public interface MessageDispatcherAnnotatedMethodDiscover {
    Method getHandler(MessageAction actionType,String parameterType);
}
