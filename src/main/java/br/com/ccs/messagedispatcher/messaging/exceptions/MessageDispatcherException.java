package br.com.ccs.messagedispatcher.messaging.exceptions;

public class MessageDispatcherException extends RuntimeException{
    public MessageDispatcherException(String message, Throwable cause) {
        super(message, cause);
    }
}
