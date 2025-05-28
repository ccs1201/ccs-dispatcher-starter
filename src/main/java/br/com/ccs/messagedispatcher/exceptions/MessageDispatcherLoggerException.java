package br.com.ccs.messagedispatcher.exceptions;

public class MessageDispatcherLoggerException extends MessageDispatcherRuntimeException {
    public MessageDispatcherLoggerException(String message, Exception e) {
        super(message, e);
    }
}
