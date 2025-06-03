package br.com.messagedispatcher.exceptions;

import br.com.messagedispatcher.model.MessageDispatcherErrorResponse;
import org.springframework.http.HttpStatus;

public class MessageDispatcherRemoteProcessException extends RuntimeException {

    private HttpStatus status = HttpStatus.UNPROCESSABLE_ENTITY;
    private String remoteCause;
    private String originService;

    public MessageDispatcherRemoteProcessException(MessageDispatcherErrorResponse errorData) {
        super(errorData.message());
        this.remoteCause = errorData.cause();
        this.originService = errorData.originService();
    }

    public MessageDispatcherRemoteProcessException(Throwable cause) {
        super(cause);
    }

    public MessageDispatcherRemoteProcessException(HttpStatus status, String message) {
        super(message);
        this.status = status;
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
