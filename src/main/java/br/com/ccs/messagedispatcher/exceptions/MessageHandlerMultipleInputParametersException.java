package br.com.ccs.messagedispatcher.exceptions;

public class MessageHandlerMultipleInputParametersException extends MessageDispatcherRuntimeException {
    public MessageHandlerMultipleInputParametersException(String message) {
        super(message);
    }
}
