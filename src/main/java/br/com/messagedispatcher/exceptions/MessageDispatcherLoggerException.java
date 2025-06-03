package br.com.messagedispatcher.exceptions;

public class MessageDispatcherLoggerException extends MessageDispatcherRuntimeException {
    public MessageDispatcherLoggerException(String message, Exception e) {
        super(message, e);
    }
}
