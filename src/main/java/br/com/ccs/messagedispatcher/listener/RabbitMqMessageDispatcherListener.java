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


package br.com.ccs.messagedispatcher.listener;

import br.com.ccs.messagedispatcher.MessageDispatcherListener;
import br.com.ccs.messagedispatcher.exceptions.MessageDispatcherLoggerException;
import br.com.ccs.messagedispatcher.messaging.model.MessageDispatcherErrorResponse;
import br.com.ccs.messagedispatcher.messaging.model.MessageWrapperResponse;
import br.com.ccs.messagedispatcher.router.MessageRouter;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Value;

import java.io.IOException;
import java.time.LocalDateTime;

import static br.com.ccs.messagedispatcher.messaging.publisher.MessageDispatcherHeaders.MESSAGE_ACTION;
import static br.com.ccs.messagedispatcher.messaging.publisher.MessageDispatcherHeaders.RESPONSE_FROM;
import static br.com.ccs.messagedispatcher.messaging.publisher.MessageDispatcherHeaders.RESPONSE_TIME_STAMP;
import static br.com.ccs.messagedispatcher.messaging.publisher.MessageDispatcherHeaders.TYPE_ID;

/**
 * Classe responsável por receber as mensagens do RabbitMQ e despachá-las para a implementação de {@code MessageRouter}.
 * <p>
 * Class responsible for receiving messages from RabbitMQ and dispatching them to the {@code MessageRouter} implementation.
 *
 * @author Cleber Souza
 * @version 1.0
 * @since 09/05/2025
 */

public class RabbitMqMessageDispatcherListener implements MessageDispatcherListener {

    private final Logger log = LoggerFactory.getLogger(RabbitMqMessageDispatcherListener.class);

    private final MessageRouter messageRouter;

    private final ObjectMapper objectMapper;

    @Value("${spring.application.name}")
    private String applicationName;

    private static final String returnExceptions = "false";

    public RabbitMqMessageDispatcherListener(MessageRouter messageRouter, ObjectMapper objectMapper) {
        this.messageRouter = messageRouter;
        this.objectMapper = objectMapper;
        log.debug("MessageDispatcherListener inicializado com o MessageRouter: {} ", messageRouter.getClass().getSimpleName());
    }

    @RabbitListener(queues = "#{@messageDispatcherProperties.queueName}",
            concurrency = "#{@messageDispatcherProperties.concurrency}",
            returnExceptions = returnExceptions)
    @Override
    public MessageWrapperResponse onMessage(Message message) {
        if (log.isDebugEnabled()) {
//            sleep();
            log(message);
        }

        var resultProcess = messageRouter.routeMessage(message);

        if (resultProcess == null) {
            return null;
        }

        //se não tiver replyTo, mas ocorrer uma exception
        //então devemos fazer o retry, se a exception persistir
        //então devemos enviar a mensagem para o DLQ
        if (resultProcess instanceof MessageDispatcherErrorResponse) {
            throw (MessageDispatcherErrorResponse) resultProcess;
        }

        if (requiresReplyTo(message)) {
            setResponseHeaders(message);
            return buildResponse(resultProcess);
        }

        return null;
    }

    private MessageWrapperResponse buildResponse(Object resultProcess) {

        if (resultProcess instanceof MessageDispatcherErrorResponse) {
            return new MessageWrapperResponse(true, resultProcess);
        }

        return new MessageWrapperResponse(false, resultProcess);
    }

    @SuppressWarnings("unussed")
    private static void sleep() {
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private static boolean requiresReplyTo(Message message) {
        return StringUtils.isNotBlank(message.getMessageProperties().getReplyTo());
    }

    private void setResponseHeaders(Message message) {
        message.getMessageProperties().setHeader(RESPONSE_TIME_STAMP, LocalDateTime.now());
        message.getMessageProperties().setHeader(RESPONSE_FROM, applicationName);
    }

    private void log(Message message) {
        try {
            log.debug("Mensagem recebida Action:{} | TypeId:{} | Body:{}",
                    message.getMessageProperties()
                            .getHeaders().get(MESSAGE_ACTION),
                    message.getMessageProperties()
                            .getHeaders().get(TYPE_ID),
                    objectMapper.readValue(message.getBody(), JsonNode.class));
        } catch (IOException e) {
            throw new MessageDispatcherLoggerException("Erro ao gerar logs", e);
        }
    }
}
