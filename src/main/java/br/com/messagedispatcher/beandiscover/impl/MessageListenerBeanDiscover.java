package br.com.messagedispatcher.beandiscover.impl;

import br.com.messagedispatcher.annotation.MessageListener;
import org.springframework.context.ApplicationContext;

import java.util.List;
import java.util.Objects;

public final class MessageListenerBeanDiscover {

    private static List<Object> listeners;

    private MessageListenerBeanDiscover() {
    }


    private static List<Object> discoverMessageListener(ApplicationContext applicationContext) {
        return applicationContext.getBeansWithAnnotation(MessageListener.class)
                .values()
                .stream()
                .toList();
    }

    public static List<Object> getMessageListeners(ApplicationContext applicationContext) {
        if (Objects.isNull(listeners)) {
            listeners = discoverMessageListener(applicationContext);
        }
        return listeners;
    }
}
