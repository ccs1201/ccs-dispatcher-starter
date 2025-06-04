package br.com.messagedispatcher.exceptions;

public class MessagePublisherException extends MessageDispatcherRuntimeException{
    public MessagePublisherException(String message, Throwable cause) {
        super(message, cause);
    }
}
