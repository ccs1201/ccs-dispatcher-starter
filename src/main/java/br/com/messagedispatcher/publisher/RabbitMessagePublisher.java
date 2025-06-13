package br.com.messagedispatcher.publisher;

import br.com.messagedispatcher.config.properties.MessageDispatcherProperties;
import br.com.messagedispatcher.publisher.proxy.TemplateProxy;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import static br.com.messagedispatcher.constants.MessageDispatcherConstants.HandlerType;
import static br.com.messagedispatcher.constants.MessageDispatcherConstants.HandlerType.*;

@Component
public final class RabbitMessagePublisher implements MessagePublisher {

    private final TemplateProxy templateProxy;
    private final String DEFAULT_EXCHANGE;
    private final String DEFAULT_ROUTING_KEY;


    public RabbitMessagePublisher(TemplateProxy templateProxy, MessageDispatcherProperties properties) {
        this.templateProxy = templateProxy;
        this.DEFAULT_EXCHANGE = properties.getExchangeName();
        this.DEFAULT_ROUTING_KEY = properties.getRoutingKey();
    }

    @Override
    public void sendEvent(final Object body) {
        this.sendEvent(DEFAULT_EXCHANGE, DEFAULT_ROUTING_KEY, body);
    }

    @Override
    public void sendEvent(final String routingKey, final Object body) {
        this.sendEvent(DEFAULT_EXCHANGE, routingKey, body);
    }

    @Override
    public void sendEvent(final String exchange, final String routingKey, final Object body) {
        this.convertAndSend(exchange, routingKey, body, EVENT);
    }

    @Override
    public void sendCommand(Object body) {
        this.sendCommand(DEFAULT_EXCHANGE, DEFAULT_ROUTING_KEY);
    }

    @Override
    public void sendCommand(String routingKey, Object body) {
        this.sendCommand(DEFAULT_EXCHANGE, routingKey, body);
    }

    @Override
    public void sendCommand(String exchange, String routingKey, Object body) {
        this.convertAndSend(exchange, routingKey, body, COMMAND);
    }

    @Override
    public <T> T doCommand(final Object body, final @NonNull Class<T> responseClass) {
        return this.convertSendAndReceive(DEFAULT_EXCHANGE, DEFAULT_ROUTING_KEY, body, responseClass, COMMAND);
    }

    @Override
    public <T> T doCommand(final String routingKey, final Object body, final @NonNull Class<T> responseClass) {
        return this.convertSendAndReceive(DEFAULT_EXCHANGE, routingKey, body, responseClass, COMMAND);
    }

    @Override
    public <T> T doCommand(final String exchange, final String routingKey, final Object body, final @NonNull Class<T> responseClass) {
        return this.convertSendAndReceive(exchange, routingKey, body, responseClass, COMMAND);
    }

    @Override
    public <T> T doQuery(final Object body, final @NonNull Class<T> responseClass) {
        return this.convertSendAndReceive(DEFAULT_EXCHANGE, DEFAULT_ROUTING_KEY, body, responseClass, QUERY);
    }

    @Override
    public <T> T doQuery(final String routingKey, final Object body, final @NonNull Class<T> responseClass) {
        return this.convertSendAndReceive(DEFAULT_EXCHANGE, routingKey, body, responseClass, QUERY);
    }

    @Override
    public <T> T doQuery(final String exchange, final String routingKey, final Object body, final @NonNull Class<T> responseClass) {
        return this.convertSendAndReceive(exchange, routingKey, body, responseClass, QUERY);
    }

    @Override
    public void sendNotification(final Object body) {
        this.convertAndSend(DEFAULT_EXCHANGE, DEFAULT_ROUTING_KEY, body, NOTIFICATION);
    }

    @Override
    public void sendNotification(final String routingKey, final Object body) {
        this.convertAndSend(DEFAULT_EXCHANGE, routingKey, body, NOTIFICATION);
    }

    private void convertAndSend(String exchangeName, String routingKey, Object body, HandlerType handlerType) {
        templateProxy.convertAndSend(exchangeName, routingKey, body, handlerType);
    }

    private <T> T convertSendAndReceive(String exchangeName, String routingKey, Object body, Class<T> responseClass, HandlerType handlerType) {
        return templateProxy.convertSendAndReceive(exchangeName, routingKey, body, responseClass, handlerType);
    }
}
