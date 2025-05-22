package br.com.ccs.dispatcher.pocs;

import br.com.ccs.dispatcher.messaging.annotation.MessageHandler;
import org.springframework.stereotype.Component;

@Component
public class UserMessageHandler {

    @MessageHandler(type = "USER_CREATED")
    public void handleUserCreated(UserCreatedPayload payload) {
        // Processa mensagem de usuário criado
    }

    @MessageHandler(type = "USER_UPDATED")
    public void handleUserUpdated(UserUpdatedPayload payload) {
        // Processa mensagem de usuário atualizado
    }
}


