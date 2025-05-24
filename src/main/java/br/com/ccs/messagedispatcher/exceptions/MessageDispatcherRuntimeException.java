package br.com.ccs.messagedispatcher.exceptions;

public class MessageDispatcherRuntimeException extends RuntimeException{
    public MessageDispatcherRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }
}
