package br.com.ccs.messagedispatcher.pocs;

import br.com.ccs.messagedispatcher.messaging.annotation.MessageListener;
import org.springframework.context.ApplicationContext;

import java.util.List;

public final class MessageListenerBeanDiscover {

    private static List<Object> listeners;

    private MessageListenerBeanDiscover() {
    }


    private static List<Object> discover(ApplicationContext applicationContext) {
        return applicationContext.getBeansWithAnnotation(MessageListener.class)
                .values()
                .stream()
                .toList();
    }

    public static List<Object> getMessageListeners(ApplicationContext applicationContext) {
        return discover(applicationContext);
    }
}
