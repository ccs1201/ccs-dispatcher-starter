package br.com.ccs.messagedispatcher.exceptions;

public class MessageDispatcherLoggerException extends MessageDispatcherRuntimeException {
    public MessageDispatcherLoggerException(Exception e) {
        super("Error logging message", e);
    }

    public MessageDispatcherLoggerException(String message, Exception e) {
        super(message, e);
    }
}
