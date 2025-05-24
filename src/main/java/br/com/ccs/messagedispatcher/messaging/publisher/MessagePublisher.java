package br.com.ccs.messagedispatcher.messaging.publisher;

import br.com.ccs.messagedispatcher.config.properties.MessageDispatcherProperties;
import br.com.ccs.messagedispatcher.messaging.MessageType;
import br.com.ccs.messagedispatcher.exceptions.MessagePublishException;
import br.com.ccs.messagedispatcher.util.httpservlet.RequestContextUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.Arrays;

import static br.com.ccs.messagedispatcher.messaging.publisher.MessageHeaders.*;

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
public class MessagePublisher {

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
            rabbitTemplate.convertAndSend(exchange, routingKey, body, m -> setMessageHeaders(body, m, null, "event", MessageType.EVENT));
        } catch (AmqpException e) {
            throw new MessagePublishException("Erro ao publicar evento " + e.getMessage(), e);
        }
    }

    public <T> T doGet(final Object body, Class<T> responseClass) {
        return fetch(HttpMethod.GET, properties.getRoutingKey(), "", body, responseClass);
    }

    public <T> T doGet(final String routingKey, final String path, final Object body, Class<T> responseClass) {
        return fetch(HttpMethod.GET, routingKey, path, body, responseClass);
    }

    public <T> T doGet(final String exchange, final String routingKey, final String path, final Object body, Class<T> responseClass) {
        return fetch(HttpMethod.GET, exchange, routingKey, path, body, responseClass);
    }

    public <T> T doPost(final String routingKey, final String path, final Object body, Class<T> responseClass) {
        return fetch(HttpMethod.POST, routingKey, path, body, responseClass);
    }

    public <T> T doPost(final String exchange, final String routingKey, final String path, final Object body, Class<T> responseClass) {
        return fetch(HttpMethod.POST, exchange, routingKey, path, body, responseClass);
    }

    public <T> T doPut(final String routingKey, final String path, final Object body, Class<T> responseClass) {
        return fetch(HttpMethod.PUT, routingKey, path, body, responseClass);
    }

    public <T> T doPut(final String exchange, final String routingKey, final String path, final Object body, Class<T> responseClass) {
        return fetch(HttpMethod.PUT, exchange, path, routingKey, body, responseClass);
    }

    public <T> T doPatch(final String routingKey, final String path, final Object body, Class<T> responseClass) {
        return fetch(HttpMethod.PATCH, routingKey, path, body, responseClass);
    }

    public <T> T doPatch(final String exchange, final String routingKey, final String path, final Object body, Class<T> responseClass) {
        return fetch(HttpMethod.PATCH, exchange, path, routingKey, body, responseClass);
    }

    public <T> T doDelete(final String routingKey, final String path, final Object body, Class<T> responseClass) {
        return fetch(HttpMethod.DELETE, routingKey, path, body, responseClass);
    }

    public <T> T doDelete(final String exchange, final String routingKey, final String path, final Object body, Class<T> responseClass) {
        return fetch(HttpMethod.DELETE, exchange, path, routingKey, body, responseClass);
    }

    /**
     * Publica uma mensagem para a aplicação local através da exchange global e espera por uma resposta.
     * <p>
     * Publishes a message to local application through the global exchange and waits for a response.
     *
     * @param body
     * @param responseClass
     * @param <T>           tipo de retorno esperado / type of expected return
     * @return (responseClass) object
     */
    public <T> T fetch(final HttpMethod method, final Object body, final String path, final @NonNull Class<T> responseClass) {
        return this.fetch(method, properties.getExchangeName(), properties.getRoutingKey(), path, body, responseClass);
    }


    /**
     * Publica uma mensagem para uma aplicação através da exchange global e espera por uma resposta.
     * <p>
     * Publishes a message to an application through the global exchange and waits for a response.
     *
     * @param routingKey
     * @param body
     * @param responseClass
     * @param <T>           tipo de retorno esperado / type of expected return
     * @return
     */
    public <T> T fetch(final HttpMethod method, final String routingKey, final String path, final Object body, final @NonNull Class<T> responseClass) {
        return this.fetch(method, properties.getExchangeName(), routingKey, path, body, responseClass);
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
    public <T> T fetch(final HttpMethod method, final String exchange, final String routingKey, String path, final Object body, final Class<T> responseClass) {
        try {
            var response = rabbitTemplate.convertSendAndReceive(exchange, routingKey, body, m -> setMessageHeaders(body, m, method, path, MessageType.RPC));

            if (response != null) {
                return objectMapper.convertValue(response, responseClass);
            }

            throw new MessagePublishException("Nenhum retorno recebido: ", null);
        } catch (AmqpException e) {
            throw new MessagePublishException("Erro ao chamar procedimento remoto " + e.getMessage(), e);
        }
    }

    private Message setMessageHeaders(Object body, Message message, HttpMethod method, String path, MessageType type) {

        var messageProperties = message.getMessageProperties();
        messageProperties.setHeader(HEADER_MESSAGE_TIMESTAMP, OffsetDateTime.now());

        if (type != MessageType.EVENT) {
            messageProperties.setHeader(HEADER_MESSAGE_METHOD, method.name());
        }

        messageProperties.setHeader(HEADER_TYPE_ID, body.getClass().getSimpleName());

        messageProperties.setHeader(HEADER_MESSAGE_TYPE, type);
        messageProperties.setHeader(HEADER_MESSAGE_PATH, path);
        messageProperties.setHeader(HEADER_MESSAGE_SOURCE, this.applicationName);

        Arrays.stream(properties.getMappedHeaders())
                .forEach(mappedHeader ->
                        RequestContextUtil.getHeader(mappedHeader)
                                .ifPresent(headerValue ->
                                        messageProperties.setHeader(mappedHeader, headerValue)));

        return message;
    }
}
