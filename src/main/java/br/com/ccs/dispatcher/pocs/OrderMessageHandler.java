package br.com.ccs.dispatcher.pocs;

import br.com.ccs.dispatcher.messaging.MessageType;
import br.com.ccs.dispatcher.messaging.annotation.MessageHandler;
import org.springframework.stereotype.Component;

@Component
public class OrderMessageHandler {

    @MessageHandler(action = MessageType.NOTIFICATION, forClass = OrderCreatedPayload.class)
    public void handleOrderCreated(OrderCreatedPayload payload) {
        // Processa mensagem de pedido criado
    }

}