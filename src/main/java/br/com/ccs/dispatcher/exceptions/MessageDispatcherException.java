package br.com.ccs.dispatcher.exceptions;

public class MessageDispatcherException extends RuntimeException {
    public MessageDispatcherException(String msg, Exception e) {
        super(msg, e);
    }
}
