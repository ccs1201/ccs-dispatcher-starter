package br.com.ccs.messagedispatcher.exceptions;

import br.com.ccs.messagedispatcher.messaging.model.MessageDispatcherErrorResponse;
import org.springframework.http.HttpStatus;

public class MessageDispatcherRemoteProcessException extends RuntimeException {

    private final HttpStatus status = HttpStatus.UNPROCESSABLE_ENTITY;
    private String remoteCause;
    private String originService;

    public MessageDispatcherRemoteProcessException(MessageDispatcherErrorResponse errorData) {
        super(errorData.message());
        this.remoteCause = errorData.cause();
        this.originService = errorData.originService();
    }

    public MessageDispatcherRemoteProcessException(String message) {
        super(message);
    }

    public MessageDispatcherRemoteProcessException(Throwable cause) {
        super(cause);
    }

    public HttpStatus getStatus() {
        return status;
    }

    public String getRemoteCause() {
        return remoteCause;
    }

    public String getOriginService() {
        return originService;
    }
}
