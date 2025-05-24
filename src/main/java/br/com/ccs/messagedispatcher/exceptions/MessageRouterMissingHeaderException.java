package br.com.ccs.messagedispatcher.exceptions;

public class MessageRouterMissingHeaderException extends MessageDispatcherRuntimeException {
    public MessageRouterMissingHeaderException(String message) {
        super(message, null);
    }
}
