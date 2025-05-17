package br.com.ccs.dispatcher.messaging.exceptions;

public class DispatcherException extends RuntimeException{
    public DispatcherException(String message, Throwable cause) {
        super(message, cause);
    }
}
