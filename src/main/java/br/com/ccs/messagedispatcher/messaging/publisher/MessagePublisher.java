package br.com.ccs.messagedispatcher.messaging.publisher;

import br.com.ccs.messagedispatcher.config.properties.MessageDispatcherProperties;
import br.com.ccs.messagedispatcher.exceptions.MessageDispatcherRemoteProcessException;
import br.com.ccs.messagedispatcher.exceptions.MessagePublishException;
import br.com.ccs.messagedispatcher.messaging.MessageAction;
import br.com.ccs.messagedispatcher.util.httpservlet.RequestContextUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.AmqpReplyTimeoutException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.RemoteInvocationResult;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.Optional;

import static br.com.ccs.messagedispatcher.messaging.publisher.MessageDispatcherHeaders.*;

/**
 * Classe de proxy para o {@link RabbitTemplate} com métodos prontos
 * para envio de mensagens a injeção dos headers definidos na propriedade
 * message.dispatcher.mapped.headers são automaticamente injetadas nos headers
 * da mensagem sendo publicada.
 * <p>
 * Proxy class for {@link RabbitTemplate} with ready-to-use methods
 * for sending messages. The headers defined in the property
 * message.dispatcher.mapped.headers are automatically injected into the headers
 * of the message being published.
 *
 * @author Cleber Souza
 * @version 1.0
 * @since 16/05/2025
 */

@Component
public final class MessagePublisher {

    private static final Logger log = LoggerFactory.getLogger(MessagePublisher.class);
    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper;
    private final MessageDispatcherProperties properties;
    @Value("${spring.application.name}")
    private String applicationName;


    public MessagePublisher(RabbitTemplate rabbitTemplate, ObjectMapper objectMapper, MessageDispatcherProperties properties) {
        this.rabbitTemplate = rabbitTemplate;
        this.objectMapper = objectMapper;
        this.properties = properties;
    }

    /**
     * Publica um evento para a aplicação local através da exchange global.
     * Atua como um fire and forget, não esperando por uma resposta.
     * <p>
     * Publishes an event to local application through the global exchange.
     * Acts as a fire and forget, not waiting for a response.
     *
     * @param body
     */
    public void sendEvent(final Object body) {
        this.sendEvent(properties.getExchangeName(), properties.getRoutingKey(), body);
    }

    /**
     * Publica um evento para uma aplicação local através da exchange global.
     * Atua como um fire and forget, não esperando por uma resposta.
     * <p>
     * Publishes an event to am application through the global exchange.
     * Acts as a fire and forget, not waiting for a response.
     *
     * @param routingKey
     * @param body
     */
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
     * @param exchange
     * @param routingKey
     * @param body
     */
    public void sendEvent(final String exchange, final String routingKey, final Object body) {
        try {
            rabbitTemplate.convertAndSend(exchange, routingKey, body, m -> setMessageHeaders(body, m, MessageAction.EVENT));
        } catch (AmqpException e) {
            throw new MessagePublishException("Erro ao publicar evento " + e.getMessage(), e);
        }
    }

    public <T> T doCommand(final Object body, final @NonNull Class<T> responseClass) {
        return this.sendAndReceive(properties.getExchangeName(), properties.getRoutingKey(), body, responseClass, MessageAction.COMMAND);
    }

    public <T> T doCommand(final String routingKey, final Object body, final @NonNull Class<T> responseClass) {
        return this.sendAndReceive(properties.getExchangeName(), routingKey, body, responseClass, MessageAction.COMMAND);
    }

    public <T> T doCommand(final String exchange, final String routingKey, final Object body, final @NonNull Class<T> responseClass) {
        return this.sendAndReceive(exchange, routingKey, body, responseClass, MessageAction.COMMAND);
    }

    public void sendCommand(final Object body) {
        this.convertAndSend(properties.getExchangeName(), properties.getRoutingKey(), body, MessageAction.COMMAND);
    }

    public void sendCommand(final String routingKey, final Object body) {
        this.convertAndSend(properties.getExchangeName(), routingKey, body, MessageAction.COMMAND);
    }

    public void sendCommand(final String exchange, final String routingKey, final Object body) {
        this.convertAndSend(exchange, routingKey, body, MessageAction.COMMAND);
    }

    public <T> T doQuery(final Object body, final @NonNull Class<T> responseClass) {
        return this.sendAndReceive(properties.getExchangeName(), properties.getRoutingKey(), body, responseClass, MessageAction.QUERY);
    }

    public <T> T doQuery(final String routingKey, final Object body, final @NonNull Class<T> responseClass) {
        return this.sendAndReceive(properties.getExchangeName(), routingKey, body, responseClass, MessageAction.QUERY);
    }

    public <T> T doQuery(final String exchange, final String routingKey, final Object body, final @NonNull Class<T> responseClass) {
        return this.sendAndReceive(exchange, routingKey, body, responseClass, MessageAction.QUERY);
    }

    public void sendNotification(final Object body) {
        this.sendNotification(properties.getRoutingKey(), body);
    }

    public void sendNotification(final String routingKey, final Object body) {
        this.convertAndSend(properties.getExchangeName(), routingKey, body, MessageAction.NOTIFICATION);
    }

    /**
     * Publica uma mensagem para uma aplicação através da exchange informada e espera por uma resposta.
     * <p>
     * Publishes a body to an application through the defined exchange and waits for a response.
     *
     * @param exchange
     * @param routingKey
     * @param body
     * @param responseClass
     * @param <T>           tipo de retorno esperado / type of expected return
     * @return (responseClass) object
     */
    private <T> T sendAndReceive(final String exchange, final String routingKey, final Object body, final Class<T> responseClass,
                                 MessageAction messageAction) {
        try {
            var response = Optional.ofNullable(
                    rabbitTemplate.convertSendAndReceive(exchange,
                            routingKey,
                            body,
                            m ->
                                    setMessageHeaders(body, m, messageAction)));

            if (response.isPresent() && response.get() instanceof RemoteInvocationResult result && result.hasException()) {
                throw new MessageDispatcherRemoteProcessException(result.getException().getMessage());
            }

            if (log.isDebugEnabled()) {
                log.debug("Resposta recebida: {}", response);
            }

            return objectMapper.convertValue(response.orElseThrow(() ->
                    new MessageDispatcherRemoteProcessException("Nenhuma resposta recebida do consumidor")), responseClass);

        } catch (AmqpReplyTimeoutException e) {
            throw new MessageDispatcherRemoteProcessException("Tempo de espera pela reposta excedido.", e, HttpStatus.REQUEST_TIMEOUT);
        } catch (Exception e) {
            throw new MessageDispatcherRemoteProcessException(e);
        }
    }

    private void convertAndSend(final String exchange, final String routingKey, final Object body, MessageAction messageAction) {
        rabbitTemplate.convertAndSend(exchange,
                routingKey,
                body,
                m ->
                        setMessageHeaders(body, m, messageAction));

    }

    private Message setMessageHeaders(Object body, Message message, MessageAction action) {
        var messageProperties = message.getMessageProperties();
        messageProperties.setHeader(MESSAGE_TIMESTAMP, OffsetDateTime.now());
        messageProperties.setHeader(TYPE_ID, body.getClass().getSimpleName());
        messageProperties.setHeader(MESSAGE_ACTION, action);
        messageProperties.setHeader(MESSAGE_SOURCE, this.applicationName);

        Arrays.stream(properties.getMappedHeaders())
                .forEach(mappedHeader ->
                        RequestContextUtil.getHeader(mappedHeader)
                                .ifPresent(headerValue ->
                                        messageProperties.setHeader(mappedHeader, headerValue)));

        return message;
    }
}
