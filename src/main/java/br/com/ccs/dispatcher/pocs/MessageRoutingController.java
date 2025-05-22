package br.com.ccs.dispatcher.pocs;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

import java.lang.reflect.Method;

@Controller
public class MessageRoutingController {

    private final MessageDispatcherBeanDiscover messageDispatcherBeanDiscover;

    public MessageRoutingController(MessageDispatcherBeanDiscover messageDispatcherBeanDiscover) {
        this.messageDispatcherBeanDiscover = messageDispatcherBeanDiscover;
    }

//    @MessageMapping("/process")
//    public void routeMessage(BaseMessage message) {
//        Method handler = messageHandlers.get(message.type());
//        if (handler != null) {
//            try {
//                Object bean = applicationContext.getBean(handler.getDeclaringClass());
//                // Converte o payload para o tipo correto e invoca o handler
//                Object typedPayload = convertPayload(message.payload(), handler.getParameterTypes()[0]);
//                handler.invoke(bean, typedPayload);
//            } catch (Exception e) {
//                throw new RuntimeException("Erro ao processar mensagem", e);
//            }
//        } else {
//            throw new IllegalArgumentException("Handler não encontrado para tipo: " + message.type());
//        }
//    }

    private Object convertPayload(Object payload, Class<?> targetType) {
        // Usar ObjectMapper ou outro mecanismo de conversão
        ObjectMapper mapper = new ObjectMapper();
        return mapper.convertValue(payload, targetType);
    }
}
