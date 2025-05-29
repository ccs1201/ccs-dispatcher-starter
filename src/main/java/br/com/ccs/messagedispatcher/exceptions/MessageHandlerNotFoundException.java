package br.com.ccs.messagedispatcher.exceptions;

public class MessageHandlerNotFoundException extends MessageRouterProcessingException {

    public MessageHandlerNotFoundException(String message) {
        super(message);
    }
}
