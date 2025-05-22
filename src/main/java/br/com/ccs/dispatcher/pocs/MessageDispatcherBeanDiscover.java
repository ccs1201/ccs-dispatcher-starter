package br.com.ccs.dispatcher.pocs;

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
        return applicationContext.getBeansWithAnnotation(Component.class)
                .values()
                .stream()
                .filter(bean -> bean.getClass().isAnnotationPresent(Component.class))
                .toList();
    }

    public List<Object> getDiscoveredBeans() {
        return discoveredBeans;
    }
}
