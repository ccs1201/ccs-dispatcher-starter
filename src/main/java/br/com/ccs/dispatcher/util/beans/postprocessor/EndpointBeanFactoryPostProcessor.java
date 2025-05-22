package br.com.ccs.dispatcher.util.beans.postprocessor;

import br.com.ccs.dispatcher.messaging.annotation.MessageListener;
import org.slf4j.Logger;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicInteger;

@Component
public class EndpointBeanFactoryPostProcessor implements BeanFactoryPostProcessor {

    private static final Logger log = org.slf4j.LoggerFactory.getLogger(EndpointBeanFactoryPostProcessor.class);

    private final AtomicInteger counter = new AtomicInteger(0);

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) {
        for (String beanName : beanFactory.getBeanDefinitionNames()) {
            BeanDefinition beanDefinition = beanFactory.getBeanDefinition(beanName);
            try {
                Class<?> beanClass = Class.forName(beanDefinition.getBeanClassName());
                if (beanClass.isAnnotationPresent(MessageListener.class)) {
                    // Adicione a lógica de processamento aqui
                    log.info("Bean {} is annotated with @MessageListener", beanClass.getName());
                    counter.incrementAndGet();
                }
            } catch (ClassNotFoundException e) {
                throw new RuntimeException("Error processing bean: " + beanName, e);
            }
        }
    }
}
