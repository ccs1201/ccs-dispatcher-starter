package br.com.ccs.dispatcher.util.beans.postprocessor;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Component;

@Component
public class EndpointBeanFactoryPostProcessor implements BeanFactoryPostProcessor {
    
    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) {
        for (String beanName : beanFactory.getBeanDefinitionNames()) {
            BeanDefinition beanDefinition = beanFactory.getBeanDefinition(beanName);
            try {
                Class<?> beanClass = Class.forName(beanDefinition.getBeanClassName());
                if (beanClass.isAnnotationPresent(MessageMapping.class)) {
                    // Faça algo com os beans anotados
                }
            } catch (ClassNotFoundException e) {
                // Tratamento de erro
            }
        }
    }
}
