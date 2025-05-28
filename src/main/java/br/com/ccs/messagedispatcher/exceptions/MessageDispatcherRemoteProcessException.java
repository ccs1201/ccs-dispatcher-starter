package br.com.ccs.messagedispatcher.exceptions;

import org.springframework.http.HttpStatus;

public class MessageDispatcherRemoteProcessException extends RuntimeException {

    private HttpStatus status = HttpStatus.UNPROCESSABLE_ENTITY;

    public HttpStatus getStatus() {
        return status;
    }

    public MessageDispatcherRemoteProcessException(String message) {
        super(message);
    }

    public MessageDispatcherRemoteProcessException(String message, Throwable cause, HttpStatus status) {
        super(message, cause);
        this.status = status;
    }

    public MessageDispatcherRemoteProcessException(Throwable cause) {
        super(cause);
    }
}
