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


package br.com.ccs.dispatcher;

import br.com.ccs.dispatcher.router.MessageRouter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;

/**
 * Classe responsável por receber as mensagens do RabbitMQ e despachá-las para o handler correto.
 * <p>
 * Class responsible for receiving RabbitMQ messages and dispatching them to the correct handler.
 *
 * @author Cleber Souza
 * @version 1.0
 * @since 09/05/2025
 */


public class DispatcherMessageHandler {

    private final Logger log = LoggerFactory.getLogger(DispatcherMessageHandler.class);

    private final MessageRouter messageRouter;

    public DispatcherMessageHandler(MessageRouter messageRouter) {
        this.messageRouter = messageRouter;
        log.info("MessageHandler inicializado.");
    }

    @RabbitListener(queues = "#{@ccsDispatcherProperties.queueName}",
            concurrency = "#{@ccsDispatcherProperties.concurrency}",
            returnExceptions = "true")
    public Object onMessage(Message message) {
        return messageRouter.handleMessage(message);
    }
}
