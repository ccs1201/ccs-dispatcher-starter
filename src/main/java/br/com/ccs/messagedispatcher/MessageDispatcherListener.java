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


package br.com.ccs.messagedispatcher;

import br.com.ccs.messagedispatcher.exceptions.MessageDispatcherLoggerException;
import br.com.ccs.messagedispatcher.messaging.publisher.MessageDispatcherHeaders;
import br.com.ccs.messagedispatcher.router.MessageRouter;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;

/**
 * Classe responsável por receber as mensagens do RabbitMQ e despachá-las para a implementação de {@code MessageRouter}.
 * <p>
 * Class responsible for receiving messages from RabbitMQ and dispatching them to the {@code MessageRouter} implementation.
 *
 * @author Cleber Souza
 * @version 1.0
 * @since 09/05/2025
 */

public class MessageDispatcherListener {

    private final Logger log = LoggerFactory.getLogger(MessageDispatcherListener.class);

    private final MessageRouter messageRouter;

    private final ObjectMapper objectMapper;


    public MessageDispatcherListener(MessageRouter messageRouter, ObjectMapper objectMapper) {
        this.messageRouter = messageRouter;
        this.objectMapper = objectMapper;
        log.info("MessageDispatcherListener inicializado com o MessageRouter: " + messageRouter.getClass().getSimpleName());
    }

    @RabbitListener(queues = "#{@messageDispatcherProperties.queueName}",
            concurrency = "#{@messageDispatcherProperties.concurrency}",
            returnExceptions = "#{@messageDispatcherProperties.returnExceptions}")
    public Object onMessage(Message message) throws JsonProcessingException {
        log(message);
        if (message.getMessageProperties().getReplyTo().isEmpty()) {
            return null;
        }
        return messageRouter.routeMessage(message);
    }

    private void log(Message message) {
        try {
            log.debug("Mensagem recebida Action:{} TypeId:{} Body:{}",
                    message.getMessageProperties()
                            .getHeaders().get(MessageDispatcherHeaders.HEADER_MESSAGE_ACTION),
                    message.getMessageProperties()
                            .getHeaders().get(MessageDispatcherHeaders.HEADER_TYPE_ID),
                    objectMapper.readValue(message.getBody(), JsonNode.class));
        } catch (Exception e) {
            throw new MessageDispatcherLoggerException("Erro ao gerar logs", e);
        }
    }
}
