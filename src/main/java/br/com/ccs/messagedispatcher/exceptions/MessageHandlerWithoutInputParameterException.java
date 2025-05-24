package br.com.ccs.messagedispatcher.exceptions;

public class MessageHandlerWithoutInputParameterException extends MessageDispatcherRuntimeException {
    public MessageHandlerWithoutInputParameterException(String message) {
        super(message, null);
    }
}
