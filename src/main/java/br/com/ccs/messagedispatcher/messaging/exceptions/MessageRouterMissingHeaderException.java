package br.com.ccs.messagedispatcher.messaging.exceptions;

public class MessageRouterMissingHeaderException extends MessageDispatcherException {
    public MessageRouterMissingHeaderException(String message) {
        super(message, null);
    }
}
