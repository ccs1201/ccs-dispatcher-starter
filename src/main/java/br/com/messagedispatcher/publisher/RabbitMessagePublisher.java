package br.com.messagedispatcher.publisher;

import br.com.messagedispatcher.config.properties.MessageDispatcherProperties;
import br.com.messagedispatcher.model.MessageType;
import br.com.messagedispatcher.publisher.proxy.TemplateProxy;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import static br.com.messagedispatcher.model.MessageType.COMMAND;
import static br.com.messagedispatcher.model.MessageType.EVENT;
import static br.com.messagedispatcher.model.MessageType.NOTIFICATION;
import static br.com.messagedispatcher.model.MessageType.QUERY;


@Component
public final class RabbitMessagePublisher implements MessagePublisher {

    private final TemplateProxy templateProxy;
    private final MessageDispatcherProperties properties;


    public RabbitMessagePublisher(TemplateProxy templateProxy, MessageDispatcherProperties properties) {
        this.templateProxy = templateProxy;
        this.properties = properties;
    }

    /**
     * Publica um evento para a aplicação local através da exchange global.
     * Atua como um fire and forget, não esperando por uma resposta.
     * <p>
     * Publishes an event to local application through the global exchange.
     * Acts as a fire and forget, not waiting for a response.
     *
     * @param body - corpo da mensagem
     */
    @Override
    public void sendEvent(final Object body) {
        this.sendEvent(properties.getExchangeName(), properties.getRoutingKey(), body);
    }

    /**
     * Publica um evento para uma aplicação através da exchange global.
     * Atua como um fire and forget, não esperando por uma resposta.
     * <p>
     * Publishes an event to am application through the global exchange.
     * Acts as a fire and forget, not waiting for a response.
     *
     * @param routingKey - chave de roteamento
     * @param body       - corpo da mensagem
     */
    @Override
    public void sendEvent(final String routingKey, final Object body) {
        this.sendEvent(properties.getExchangeName(), routingKey, body);
    }

    /**
     * Publica um evento para uma aplicação através da exchange informada.
     * Atua como um fire and forget, não esperando por uma resposta.
     * <p>
     * Publishes an event to an application through the defined exchange.
     * Acts as a fire and forget, not waiting for a response.
     *
     * @param exchange   - nome da exchange
     * @param routingKey - chave de roteamento
     * @param body       - corpo da mensagem
     */
    @Override
    public void sendEvent(final String exchange, final String routingKey, final Object body) {
        this.convertAndSend(exchange, routingKey, body, EVENT);
    }

    @Override
    public <T> T doCommand(final Object body, final @NonNull Class<T> responseClass) {
        return this.convertSendAndReceive(properties.getExchangeName(), properties.getRoutingKey(), body, responseClass, COMMAND);
    }

    @Override
    public <T> T doCommand(final String routingKey, final Object body, final @NonNull Class<T> responseClass) {
        return this.convertSendAndReceive(properties.getExchangeName(), routingKey, body, responseClass, COMMAND);
    }

    @Override
    public <T> T doCommand(final String exchange, final String routingKey, final Object body, final @NonNull Class<T> responseClass) {
        return this.convertSendAndReceive(exchange, routingKey, body, responseClass, COMMAND);
    }

    @Override
    public <T> T doQuery(final Object body, final @NonNull Class<T> responseClass) {
        return this.convertSendAndReceive(properties.getExchangeName(), properties.getRoutingKey(), body, responseClass, QUERY);
    }

    @Override
    public <T> T doQuery(final String routingKey, final Object body, final @NonNull Class<T> responseClass) {
        return this.convertSendAndReceive(properties.getExchangeName(), routingKey, body, responseClass, QUERY);
    }

    @Override
    public <T> T doQuery(final String exchange, final String routingKey, final Object body, final @NonNull Class<T> responseClass) {
        return this.convertSendAndReceive(exchange, routingKey, body, responseClass, QUERY);
    }

    @Override
    public void sendNotification(final Object body) {
        this.convertAndSend(properties.getExchangeName(), properties.getRoutingKey(), body, NOTIFICATION);
    }

    @Override
    public void sendNotification(final String routingKey, final Object body) {
        this.convertAndSend(properties.getExchangeName(), routingKey, body, NOTIFICATION);
    }

    private void convertAndSend(String exchangeName, String routingKey, Object body, MessageType messageType) {
        templateProxy.convertAndSend(exchangeName, routingKey, body, messageType);
    }

    private <T> T convertSendAndReceive(String exchangeName, String routingKey, Object body, Class<T> responseClass, MessageType messageType) {
        return templateProxy.convertSendAndReceive(exchangeName, routingKey, body, responseClass, messageType);
    }
}
