package br.com.ccs.dispatcher.config.rabbitmq;

import br.com.ccs.dispatcher.config.properties.DispatcherConfigurationProperties;
import br.com.ccs.dispatcher.exceptions.MessagePublishException;
import br.com.ccs.dispatcher.util.httpservlet.RequestContextUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import java.util.Arrays;

/**
 * /**
 * Classe de proxy para o {@link RabbitTemplate} com métodos prontos
 * para envio de mensagens a injeção dos headers definidos na propriedade
 * ccs.dispatcher.mapped.headers são automaticamente injetadas nos headers
 * da mensagem sendo publicada.
 * <p>
 * Proxy class for {@link RabbitTemplate} with ready-to-use methods
 * for sending messages. The headers defined in the property
 * ccs.dispatcher.mapped.headers are automatically injected into the headers
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
    private final DispatcherConfigurationProperties properties;
    @Value("${spring.application.name}")
    private String applicationName;


    public MessagePublisher(RabbitTemplate rabbitTemplate, ObjectMapper objectMapper, DispatcherConfigurationProperties properties) {
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
            rabbitTemplate.convertAndSend(exchange, routingKey, body, this::setMessageHeaders);
        } catch (AmqpException e) {
            throw new MessagePublishException("Erro ao publicar evento " + e.getMessage(), e);
        }
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
    public <T> T fetch(final Object body, final @NonNull Class<T> responseClass) {
        return this.fetch(properties.getExchangeName(), properties.getRoutingKey(), body, responseClass);
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
    public <T> T fetch(final String routingKey, final Object body, final @NonNull Class<T> responseClass) {
        return this.fetch(properties.getExchangeName(), routingKey, body, responseClass);
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
    public <T> T fetch(final String exchange, final String routingKey, final Object body, final Class<T> responseClass) {
        try {
            var response = rabbitTemplate.convertSendAndReceive(exchange, routingKey, body, this::setMessageHeaders);

            if (response != null) {
                return objectMapper.convertValue(response, responseClass);
            }

            throw new MessagePublishException("Nenhum retorno recebido: ", null);
        } catch (AmqpException e) {
            throw new MessagePublishException("Erro ao publicar evento " + e.getMessage(), e);
        }
    }

    private Message setMessageHeaders(Message message) {

        var messageProperties = message.getMessageProperties();

        messageProperties.setHeader("x-dispatcher-origin", this.applicationName);

        Arrays.stream(properties
                        .getMappedHeaders())
                .forEach(mappedheader -> {
                    RequestContextUtil.getHeader(mappedheader)
                            .ifPresent(headerValue -> messageProperties.setHeader(mappedheader, headerValue));
                });
        return message;
    }
}
