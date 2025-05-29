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

package br.com.ccs.messagedispatcher.exceptions;

/**
 * Exceção lançada quando ocorre um erro no envio de mensagens para a fila RabbitMQ.
 * <p>
 * Exception thrown when an error occurs while sending messages to the RabbitMQ queue.
 *
 * @author Cleber Souza
 * @version 1.0
 * @since 09/05/2025
 */

public class MessageRouterProcessingException extends MessageDispatcherRuntimeException {
    public MessageRouterProcessingException(String msg, Throwable e) {
        super(msg, e);
    }

    public MessageRouterProcessingException(String message) {
        super(message);
    }
}
