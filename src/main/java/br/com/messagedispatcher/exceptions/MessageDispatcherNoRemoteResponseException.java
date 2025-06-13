package br.com.messagedispatcher.exceptions;

import org.springframework.http.HttpStatus;

public class MessageDispatcherNoRemoteResponseException extends MessageDispatcherRemoteProcessException {

    public MessageDispatcherNoRemoteResponseException(HttpStatus status, String remoteService) {
        super(status, "Nenhuma resposta do serviço remoto", remoteService);
    }
}
