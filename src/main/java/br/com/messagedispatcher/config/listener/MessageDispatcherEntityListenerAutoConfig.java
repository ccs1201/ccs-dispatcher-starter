package br.com.messagedispatcher.config.listener;

import br.com.messagedispatcher.config.properties.EntityEventsProperties;
import br.com.messagedispatcher.listener.MessageDispatcherEntityEventsListener;
import br.com.messagedispatcher.listener.MessageDispatcherEntityEventsListenerImpl;
import br.com.messagedispatcher.publisher.MessagePublisher;
import org.slf4j.Logger;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(name = "message.dispatcher.entity-events.enabled", havingValue = "true")
public class MessageDispatcherEntityListenerAutoConfig {

    private final Logger log = org.slf4j.LoggerFactory.getLogger(MessageDispatcherEntityListenerAutoConfig.class);


    @Bean
    public MessageDispatcherEntityEventsListener messageDispatcherEntityListener(MessagePublisher publisher, EntityEventsProperties properties) {
        log.debug("Inicializando Entity Listener.");
        return new MessageDispatcherEntityEventsListenerImpl(publisher, properties);
    }
}