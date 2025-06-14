package br.com.messagedispatcher.config.listener;

import br.com.messagedispatcher.integrator.MessageDispatcherIntegratorProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.orm.jpa.HibernatePropertiesCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(name = "message.dispatcher.entity-events.enabled", havingValue = "true")
public class HibernateListenerAutoConfig {

    @Bean
    public HibernatePropertiesCustomizer hibernatePropertiesCustomizer(MessageDispatcherIntegratorProvider integratorProvider) {
        return hibernateProperties -> hibernateProperties.put("hibernate.integrator_provider", integratorProvider);
    }
}
