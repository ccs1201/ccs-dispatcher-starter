package br.com.ccs.dispatcher.pocs;

import br.com.ccs.dispatcher.messaging.annotation.Event;
import br.com.ccs.dispatcher.messaging.annotation.MessageListener;

@MessageListener
public class UserMessageHandler {

    @Event(type = "USER_CREATED")
    public void handleUserCreated(UserCreatedPayload payload) {
        // Processa mensagem de usuário criado
    }

    @Event(type = "USER_UPDATED")
    public void handleUserUpdated(UserUpdatedPayload payload) {
        // Processa mensagem de usuário atualizado
    }
}


