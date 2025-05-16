package br.com.ccs.dispatcher.exceptions;

public class DispatcherException extends RuntimeException{
    public DispatcherException(String message, Throwable cause) {
        super(message, cause);
    }
}
