package br.com.ccs.dispatcher.pocs;

import br.com.ccs.dispatcher.messaging.annotation.MessageListener;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MessageDispatcherBeanDiscover {

    private final List<Object> discoveredBeans;

    public MessageDispatcherBeanDiscover(ApplicationContext applicationContext) {
        this.discoveredBeans = discover(applicationContext);
    }


    public List<Object> discover(ApplicationContext applicationContext) {
        return applicationContext.getBeansWithAnnotation(MessageListener.class)
                .values()
                .stream()
                .filter(bean -> bean.getClass().isAnnotationPresent(MessageListener.class))
                .toList();
    }

    public List<Object> getDiscoveredBeans() {
        return discoveredBeans;
    }
}
