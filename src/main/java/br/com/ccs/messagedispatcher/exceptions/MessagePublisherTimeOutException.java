package br.com.ccs.messagedispatcher.exceptions;

import org.springframework.http.HttpStatus;

public class MessagePublisherTimeOutException extends MessagePublisherException {

    private final HttpStatus httpStatus = HttpStatus.REQUEST_TIMEOUT;

    public MessagePublisherTimeOutException(String message, Throwable cause) {
        super(message, cause);
    }

    public HttpStatus getStatus() {
        return httpStatus;
    }
}
