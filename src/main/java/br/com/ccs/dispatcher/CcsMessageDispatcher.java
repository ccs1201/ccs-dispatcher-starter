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

import br.com.ccs.dispatcher.exceptions.MessageDispatcherException;
import br.com.ccs.dispatcher.model.MessageWrapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.servlet.HandlerExecutionChain;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.util.logging.Logger;

/**
 * Classe responsável por receber as mensagens do RabbitMQ e despachá-las para o handler correto.
 * <p>
 * Class responsible for receiving RabbitMQ messages and dispatching them to the correct handler.
 *
 * @author Cleber Souza
 * @version 1.0
 * @since 09/05/2025
 */
public class CcsMessageDispatcher {

    private static final Logger log = Logger.getLogger(CcsMessageDispatcher.class.getName());

    private final ObjectMapper objectMapper;
    private final RequestMappingHandlerMapping handlerMapping;
    private final RequestMappingHandlerAdapter handlerAdapter;

    public CcsMessageDispatcher(ObjectMapper objectMapper,
                                RequestMappingHandlerMapping handlerMapping,
                                RequestMappingHandlerAdapter handlerAdapter) {
        log.info("Iniciando CcsMessageDispatcher");
        this.objectMapper = objectMapper;
        this.handlerMapping = handlerMapping;
        this.handlerAdapter = handlerAdapter;
        log.info("CcsMessageDispatcher inicializado.");
    }

    @RabbitListener(queues = "#{@ccsDispatcherProperties.queueName}",
            concurrency = "#{@ccsDispatcherProperties.concurrency}",
            returnExceptions = "true")
    public Object onMessage(Message message) {

        try {
            MessageWrapper messageWrapper = objectMapper.readValue(message.getBody(), MessageWrapper.class);
            log.info("Mensagem recebida: " + messageWrapper);

            // Cria request com método e path
            MockHttpServletRequest request = new MockHttpServletRequest(
                    messageWrapper.getMethod(),
                    messageWrapper.getPath()
            );

            // Configura content type e corpo da requisição
            String contentType = messageWrapper.getHeaders() != null ?
                    messageWrapper.getHeaders().get("Content-Type") : MessageProperties.CONTENT_TYPE_JSON;
            request.setContentType(contentType != null ? contentType : MessageProperties.CONTENT_TYPE_JSON);

            if (messageWrapper.getBody() != null) {
                byte[] bodyContent = objectMapper.writeValueAsBytes(messageWrapper.getBody());
                request.setContent(bodyContent);
            }

            // Adiciona headers
            if (messageWrapper.getHeaders() != null) {
                messageWrapper.getHeaders().forEach(request::addHeader);
            }

//            // Adiciona query parameters se existirem
//            if (messageWrapper.getQueryParams() != null) {
//                messageWrapper.getQueryParams().forEach(request::addParameter);
//            }

            // Busca o handler
            HandlerExecutionChain handler = handlerMapping.getHandler(request);
            if (handler == null) {
                throw new IllegalArgumentException(
                        "Nenhum handler encontrado para path: " + messageWrapper.getPath()
                );
            }

            // Executa a requisição
            MockHttpServletResponse response = new MockHttpServletResponse();
            handlerAdapter.handle(request, response, handler.getHandler());

            // Processa a resposta
            byte[] responseBody = response.getContentAsByteArray();
            if (responseBody.length > 0) {
                String responseContentType = response.getContentType();
                if (responseContentType != null && responseContentType.contains(MessageProperties.CONTENT_TYPE_JSON)) {
                    return objectMapper.readValue(responseBody, Object.class);
                }
                return responseBody;
            }

            return null;

        } catch (Exception e) {
            log.severe("Erro ao processar mensagem: " + e.getMessage());
            throw new MessageDispatcherException("Erro ao processar mensagem", e);
        }
    }
}
