package br.com.messagedispatcher.exceptions;

public class MessageDispatcherRuntimeException extends RuntimeException {

    public MessageDispatcherRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }

    public MessageDispatcherRuntimeException(String message) {
        super(message);
    }

    public MessageDispatcherRuntimeException(Throwable cause) {
        super(cause);
    }
}
