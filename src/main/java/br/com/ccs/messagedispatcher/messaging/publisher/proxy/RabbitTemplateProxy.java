package br.com.ccs.messagedispatcher.messaging.publisher.proxy;

import br.com.ccs.messagedispatcher.config.properties.MessageDispatcherProperties;
import br.com.ccs.messagedispatcher.exceptions.MessageDispatcherRemoteProcessException;
import br.com.ccs.messagedispatcher.exceptions.MessagePublisherTimeOutException;
import br.com.ccs.messagedispatcher.messaging.MessageType;
import br.com.ccs.messagedispatcher.messaging.model.MessageDispatcherErrorResponse;
import br.com.ccs.messagedispatcher.messaging.model.MessageWrapperResponse;
import br.com.ccs.messagedispatcher.util.EnvironmentUtils;
import br.com.ccs.messagedispatcher.util.httpservlet.RequestContextUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.AmqpRemoteException;
import org.springframework.amqp.core.AmqpReplyTimeoutException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.Optional;

import static br.com.ccs.messagedispatcher.messaging.publisher.MessageDispatcherHeaders.MESSAGE_KINDA;
import static br.com.ccs.messagedispatcher.messaging.publisher.MessageDispatcherHeaders.MESSAGE_SOURCE;
import static br.com.ccs.messagedispatcher.messaging.publisher.MessageDispatcherHeaders.MESSAGE_TIMESTAMP;
import static br.com.ccs.messagedispatcher.messaging.publisher.MessageDispatcherHeaders.TYPE_ID;

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
@SuppressWarnings("unused")
@Component
public class RabbitTemplateProxy implements TemplateProxy {

    private static final Logger log = LoggerFactory.getLogger(RabbitTemplateProxy.class);

    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper;
    private final MessageDispatcherProperties properties;


    public RabbitTemplateProxy(RabbitTemplate rabbitTemplate, ObjectMapper objectMapper, MessageDispatcherProperties properties) {
        this.rabbitTemplate = rabbitTemplate;
        this.objectMapper = objectMapper;
        this.properties = properties;
    }

    @Override
    public <T> T convertSendAndReceive(final String exchange, final String routingKey, final Object body, final Class<T> responseClass,
                                       MessageType messageType) {
        return this.sendAndReceive(exchange, routingKey, body, responseClass, messageType);
    }

    @Override
    public void convertAndSend(final String exchange, final String routingKey, final Object body, MessageType messageType) {
        this.send(exchange, routingKey, body, messageType);
    }


    private <T> T sendAndReceive(final String exchange, final String routingKey, final Object body, final Class<T> responseClass,
                                 MessageType messageType) {
        try {

            var response = Optional.ofNullable(
                    (MessageWrapperResponse) rabbitTemplate.convertSendAndReceive(exchange,
                            routingKey,
                            body,
                            m ->
                                    setMessageHeaders(body, m, messageType)));

            var messageWrapperResponse = objectMapper
                    .convertValue(response.orElseThrow(() ->
                            new MessageDispatcherRemoteProcessException(HttpStatus.FAILED_DEPENDENCY, "Nenhuma resposta recebida do consumidor")), MessageWrapperResponse.class);

            if (log.isDebugEnabled()) {
                log.debug("Resposta recebida: {}", response.get());
            }
            if (messageWrapperResponse.hasError()) {
                var errorData = objectMapper.convertValue(messageWrapperResponse.data(), MessageDispatcherErrorResponse.class);
                throw new MessageDispatcherRemoteProcessException(errorData);
            }

            return objectMapper.convertValue(messageWrapperResponse.data(), responseClass);
        } catch (AmqpReplyTimeoutException e) {
            throw new MessagePublisherTimeOutException("Tempo de espera pela reposta excedido.", e);
        } catch (AmqpRemoteException e) {
            throw new MessageDispatcherRemoteProcessException(e.getCause());
        }
    }

    private void send(final String exchange, final String routingKey, final Object body, MessageType messageType) {
        rabbitTemplate.convertAndSend(exchange,
                routingKey,
                body,
                m ->
                        setMessageHeaders(body, m, messageType));
    }

    private Message setMessageHeaders(Object body, Message message, MessageType action) {
        var messageProperties = message.getMessageProperties();
        messageProperties.setHeader(MESSAGE_TIMESTAMP, OffsetDateTime.now());
        messageProperties.setHeader(TYPE_ID, body.getClass().getSimpleName());
        messageProperties.setHeader(MESSAGE_KINDA, action);
        messageProperties.setHeader(MESSAGE_SOURCE, EnvironmentUtils.getAppName());

        Arrays.stream(properties.getMappedHeaders())
                .forEach(mappedHeader ->
                        RequestContextUtil.getHeader(mappedHeader)
                                .ifPresent(headerValue ->
                                        messageProperties.setHeader(mappedHeader, headerValue)));

        return message;
    }

}
