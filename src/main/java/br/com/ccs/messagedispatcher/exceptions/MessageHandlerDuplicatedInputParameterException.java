package br.com.ccs.messagedispatcher.exceptions;

public class MessageHandlerDuplicatedInputParameterException extends MessageDispatcherRuntimeException {
    public MessageHandlerDuplicatedInputParameterException(String message) {
        super(message);
    }
}
