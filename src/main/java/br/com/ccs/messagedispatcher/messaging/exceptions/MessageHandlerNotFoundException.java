package br.com.ccs.messagedispatcher.messaging.exceptions;

public class MessageHandlerNotFoundException extends MessageDispatcherException {

    public MessageHandlerNotFoundException(String message) {
        super(message, null);
    }
}
