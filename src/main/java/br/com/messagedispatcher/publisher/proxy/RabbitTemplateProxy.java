package br.com.messagedispatcher.publisher.proxy;

import br.com.messagedispatcher.config.properties.MessageDispatcherProperties;
import br.com.messagedispatcher.exceptions.MessageDispatcherRemoteProcessException;
import br.com.messagedispatcher.exceptions.MessagePublisherException;
import br.com.messagedispatcher.exceptions.MessagePublisherTimeOutException;
import br.com.messagedispatcher.model.HandlerType;
import br.com.messagedispatcher.model.MessageDispatcherRemoteInvocationResult;
import br.com.messagedispatcher.util.EnvironmentUtils;
import br.com.messagedispatcher.util.httpservlet.RequestContextUtil;
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

import static br.com.messagedispatcher.constants.MessageDispatcherConstants.MessageDispatcherHeaders.BODY_TYPE_HEADER;
import static br.com.messagedispatcher.constants.MessageDispatcherConstants.MessageDispatcherHeaders.HANDLER_TYPE_HEADER;
import static br.com.messagedispatcher.constants.MessageDispatcherConstants.MessageDispatcherHeaders.MESSAGE_SOURCE_HEADER;
import static br.com.messagedispatcher.constants.MessageDispatcherConstants.MessageDispatcherHeaders.MESSAGE_TIMESTAMP_HEADER;
import static java.util.Objects.nonNull;

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
                                       HandlerType handlerType) {
        return this.sendAndReceive(exchange, routingKey, body, responseClass, handlerType);
    }

    @Override
    public void convertAndSend(final String exchange, final String routingKey, final Object body, HandlerType handlerType) {
        this.send(exchange, routingKey, body, handlerType);
    }


    private <T> T sendAndReceive(final String exchange, final String routingKey, final Object body, final Class<T> responseClass,
                                 HandlerType handlerType) {
        try {

            var response = Optional.ofNullable(
                    rabbitTemplate.convertSendAndReceive(exchange,
                            routingKey,
                            body,
                            message ->
                                    setMessageHeaders(body, message, handlerType, exchange, routingKey)));

            var remoteInvocationResult = objectMapper
                    .convertValue(response.orElseThrow(() ->
                            new MessageDispatcherRemoteProcessException(HttpStatus.FAILED_DEPENDENCY, "Nenhuma resposta recebida do consumidor", routingKey)), MessageDispatcherRemoteInvocationResult.class);

            if (log.isDebugEnabled()) {
                log.debug("Resposta recebida: {}", remoteInvocationResult);
            }
            if (remoteInvocationResult.hasException()) {
                throw new MessageDispatcherRemoteProcessException(remoteInvocationResult.exception(), remoteInvocationResult.remoteService());
            }

            return objectMapper.convertValue(remoteInvocationResult.value(), responseClass);
        } catch (AmqpReplyTimeoutException e) {
            throw new MessagePublisherTimeOutException("Tempo de espera pela reposta excedido.", e);
        } catch (AmqpRemoteException e) {
            throw new MessagePublisherException("Erro ao publicar mensagem.", e.getCause());
        }
    }

    private void send(final String exchange, final String routingKey, final Object body, HandlerType handlerType) {
        rabbitTemplate.convertAndSend(exchange,
                routingKey,
                body,
                message ->
                        setMessageHeaders(body, message, handlerType, exchange, routingKey));
    }

    private Message setMessageHeaders(final Object body, final Message message, final HandlerType handlerType,
                                      final String exchange, final String routingKey) {

        var messageProperties = message.getMessageProperties();
        messageProperties.setHeader(MESSAGE_TIMESTAMP_HEADER, OffsetDateTime.now());
        messageProperties.setHeader(BODY_TYPE_HEADER, body.getClass().getSimpleName());
        messageProperties.setHeader(HANDLER_TYPE_HEADER, handlerType);
        messageProperties.setHeader(MESSAGE_SOURCE_HEADER, EnvironmentUtils.getAppName());

        if (nonNull(properties.getMappedHeaders())) {
            Arrays.stream(properties.getMappedHeaders())
                    .forEach(mappedHeader ->
                            RequestContextUtil.getHeader(mappedHeader)
                                    .ifPresent(headerValue ->
                                            messageProperties.setHeader(mappedHeader, headerValue)));
        }

        if (log.isDebugEnabled()) {
            log.debug("""
                            
                                Mensagem enviada ao Broker:
                                Exchange: {}
                                RoutingKey: {}
                                Headers: {}
                                Body: {}
                            """,
                    exchange,
                    routingKey,
                    message.getMessageProperties().getHeaders(),
                    body);
        }

        return message;
    }

}
