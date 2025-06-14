package br.com.messagedispatcher.config;

import br.com.messagedispatcher.config.properties.EntityEventsProperties;
import br.com.messagedispatcher.util.factory.ExchangeFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Declarables;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(name = "message.dispatcher.entity-events.enabled", havingValue = "true")
public class EntityEventsPublishExchangeAutoConfig {

    private static final Logger log = LoggerFactory.getLogger(EntityEventsPublishExchangeAutoConfig.class);

    @Bean
    public Declarables entityEventsPublishExchange(EntityEventsProperties entityEventsProperties) {
        var exchange = ExchangeFactory.buildExchange(entityEventsProperties.getExchange(),
                true, entityEventsProperties.getExchangeType(), null);
        log.info("Entity Events Publish Exchange criada: {}", exchange);
        return new Declarables(exchange);
    }
}
