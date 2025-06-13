package br.com.messagedispatcher.exceptions;

import org.springframework.http.HttpStatus;

public class MessageDispatcherRemoteProcessException extends MessageDispatcherRuntimeException {

    private HttpStatus status = HttpStatus.UNPROCESSABLE_ENTITY;
    private final String remoteService;


    public MessageDispatcherRemoteProcessException(Throwable cause, String remoteService) {
        super(cause.getMessage(), cause);
        this.remoteService = remoteService;
    }

    public MessageDispatcherRemoteProcessException(HttpStatus status, String message, String remoteService) {
        super(message);
        this.status = status;
        this.remoteService = remoteService;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public String getRemoteService() {
        return remoteService;
    }
}
