package br.com.messagedispatcher.config.properties;

import br.com.messagedispatcher.constants.MessageDispatcherConstants;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "message.dispatcher.entity-events")
public class EntityEventsProperties {

    /**
     * Exchange para onde os eventos de entidade serão enviados
     */
    private String exchange;

    /**
     * Routing key para onde os eventos de entidade serão enviados
     */
    private String routingKey;

    /**
     * Tipo de exchange para onde os eventos de entidade serão enviados
     */
    private MessageDispatcherConstants.Exchange exchangeType = MessageDispatcherConstants.Exchange.TOPIC;

    /**
     * Habilita o listener e publicação de eventos de entidade
     */
    private boolean enabled = false;

    @Value("${spring.application.name}")
    private String appName;

    @PostConstruct
    public void init() {
        if (exchange == null) {
            exchange = appName + "-entity-events";
        }

        if (routingKey == null) {
            routingKey = "#";
        }
    }

    public String getExchange() {
        return exchange;
    }

    public void setExchange(String exchange) {
        this.exchange = exchange;
    }

    public String getRoutingKey() {
        return routingKey;
    }

    public void setRoutingKey(String routingKey) {
        this.routingKey = routingKey;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public MessageDispatcherConstants.Exchange getExchangeType() {
        return exchangeType;
    }

    public void setExchangeType(MessageDispatcherConstants.Exchange exchangeType) {
        this.exchangeType = exchangeType;
    }
}