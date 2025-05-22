package br.com.ccs.dispatcher.pocs;

import br.com.ccs.dispatcher.messaging.annotation.MessageHandler;
import org.springframework.stereotype.Component;

@Component
public class OrderMessageHandler {

    @MessageHandler(type = "ORDER_CREATED")
    public void handleOrderCreated(OrderCreatedPayload payload) {
        // Processa mensagem de pedido criado
    }

}