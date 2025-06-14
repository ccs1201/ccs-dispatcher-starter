/*
 * Copyright 2024 Cleber Souza
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package br.com.messagedispatcher.listener;

import br.com.messagedispatcher.MessageDispatcherListener;
import br.com.messagedispatcher.exceptions.MessageDispatcherLoggerException;
import br.com.messagedispatcher.model.MessageDispatcherRemoteInvocationResult;
import br.com.messagedispatcher.router.MessageRouter;
import br.com.messagedispatcher.util.MessageDispatcherUtils;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;

import static br.com.messagedispatcher.constants.MessageDispatcherConstants.Headers.*;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

/**
 * Classe responsável por receber as mensagens do RabbitMQ e despachá-las para a implementação de {@link MessageRouter}.
 * <p>
 * Class responsible for receiving messages from RabbitMQ and dispatching them to the {@link MessageRouter} implementation.
 *
 * @author Cleber Souza
 * @version 1.0
 * @since 09/05/2025
 */

@Component
@ConditionalOnProperty(prefix = "message.dispatcher", name = "default-listener-enabled", havingValue = "true", matchIfMissing = true)
public class RabbitMqMessageDispatcherListener implements MessageDispatcherListener {

    private final Logger log = LoggerFactory.getLogger(RabbitMqMessageDispatcherListener.class);

    private final MessageRouter messageRouter;

    private final ObjectMapper objectMapper;

    private static final String returnExceptions = "false";

    public RabbitMqMessageDispatcherListener(MessageRouter messageRouter, ObjectMapper objectMapper) {
        this.messageRouter = messageRouter;
        this.objectMapper = objectMapper;
        log.debug("RabbitMqMessageDispatcherListener inicializado com o MessageRouter: {} ", messageRouter.getClass().getSimpleName());
    }

    @RabbitListener(queues = "#{@messageDispatcherProperties.queueName}",
            concurrency = "#{@messageDispatcherProperties.concurrency}",
            returnExceptions = returnExceptions, errorHandler = "messageDispatcherErrorHandler")
    @Override
    public MessageDispatcherRemoteInvocationResult onMessage(Message message) {
        if (log.isDebugEnabled()) {
//            sleep();
            log(message);
        }

        var resultProcess = messageRouter.routeMessage(message);

        if (resultProcess == null) {
            return null;
        }

        if (requiresReplyTo(message)) {
            setResponseHeaders(message);
            return buildResponse(resultProcess);
        }

        return null;
    }

    private MessageDispatcherRemoteInvocationResult buildResponse(Object resultProcess) {
        return MessageDispatcherRemoteInvocationResult.of(resultProcess);
    }

    @SuppressWarnings("unused")
    private static void sleep() {
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private static boolean requiresReplyTo(Message message) {
        return isNotBlank(message.getMessageProperties().getReplyTo());
    }

    private void setResponseHeaders(Message message) {
        message.getMessageProperties().setHeader(RESPONSE_TIME_STAMP.getHeaderName(), LocalDateTime.now());
        message.getMessageProperties().setHeader(RESPONSE_FROM.getHeaderName(), MessageDispatcherUtils.getAppName());
    }

    private void log(Message message) {
        try {
            log.debug("Mensagem recebida HandlerType:{} | BodyType:{} | Body:{}",
                    message.getMessageProperties()
                            .getHeaders().get(HANDLER_TYPE.getHeaderName()),
                    message.getMessageProperties()
                            .getHeaders().get(BODY_TYPE.getHeaderName()),
                    objectMapper.readValue(message.getBody(), JsonNode.class));
        } catch (IOException e) {
            throw new MessageDispatcherLoggerException("Erro ao gerar logs", e);
        }
    }
}
