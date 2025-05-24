package br.com.ccs.messagedispatcher.exceptions;

public class MessageHandlerDuplicatedParameterException extends MessageDispatcherRuntimeException {
    public MessageHandlerDuplicatedParameterException(String message) {
        super(message, null);
    }
}
