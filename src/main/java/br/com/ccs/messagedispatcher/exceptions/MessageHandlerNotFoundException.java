package br.com.ccs.messagedispatcher.exceptions;

public class MessageHandlerNotFoundException extends MessageDispatcherRuntimeException {

    public MessageHandlerNotFoundException(String message) {
        super(message);
    }
}
