package br.com.messagedispatcher.exceptions;

public class MessageHandlerNotFoundException extends MessageRouterProcessingException {

    public MessageHandlerNotFoundException(String message) {
        super(message);
    }
}
