
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
        log.info("Mensagem recebida: " + message);

        try {
            MessageWrapper messageWrapper = objectMapper.readValue(message.getBody(), MessageWrapper.class);

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
