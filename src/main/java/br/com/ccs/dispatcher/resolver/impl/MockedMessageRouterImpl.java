package br.com.ccs.dispatcher.resolver.impl;

import br.com.ccs.dispatcher.exceptions.MessageRouterException;
import br.com.ccs.dispatcher.model.MessageWrapper;
import br.com.ccs.dispatcher.resolver.MessageRouter;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerExecutionChain;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

@Component
public class MockedMessageRouterImpl implements MessageRouter {

    private final Logger log = LoggerFactory.getLogger(MockedMessageRouterImpl.class);
    private final ObjectMapper objectMapper;
    private final RequestMappingHandlerMapping handlerMapping;
    private final RequestMappingHandlerAdapter handlerAdapter;

    public MockedMessageRouterImpl(ObjectMapper objectMapper, RequestMappingHandlerMapping handlerMapping,
                                   RequestMappingHandlerAdapter handlerAdapter) {
        this.objectMapper = objectMapper;
        this.handlerMapping = handlerMapping;
        this.handlerAdapter = handlerAdapter;
    }

    public Object handleMessage(Message message) {
        var messageWrapper = getMessageWrapper(message);
        try {
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
                request.setContent(messageWrapper.getBody().getBytes(StandardCharsets.UTF_8));
            }

            // Adiciona headers
            if (messageWrapper.getHeaders() != null) {
                messageWrapper.getHeaders().forEach(request::addHeader);
            }

            // Adiciona query parameters se existirem
            if (messageWrapper.getQueryParams() != null) {
                messageWrapper.getQueryParams().forEach(request::addParameter);
            }

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
            log.error("Erro ao processar mensagem: {}", e.getMessage());
            throw new MessageRouterException("Nenhum handler encontrado para path: ".concat(messageWrapper.getPath()), e);
        }
    }

    private MessageWrapper getMessageWrapper(Message message) {
        try {
            var messageWrapper = objectMapper.readValue(message.getBody(), MessageWrapper.class);
            log.info("Mensagem recebida: " + messageWrapper);
            return messageWrapper;
        } catch (IOException e) {
            throw new MessageRouterException("Erro ao ler mensagem: " + Arrays.toString(message.getBody()), e);
        }
    }
}