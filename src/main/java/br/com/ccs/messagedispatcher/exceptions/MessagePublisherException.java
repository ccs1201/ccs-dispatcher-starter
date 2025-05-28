package br.com.ccs.messagedispatcher.exceptions;

public class MessagePublisherException extends MessageDispatcherRuntimeException{
    public MessagePublisherException(String message, Throwable cause) {
        super(message, cause);
    }
}
