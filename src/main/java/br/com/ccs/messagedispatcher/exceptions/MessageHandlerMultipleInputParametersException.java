package br.com.ccs.messagedispatcher.exceptions;

public class MessageHandlerMultipleInputParametersException extends MessageDispatcherBeanResolutionException {
    public MessageHandlerMultipleInputParametersException(String message) {
        super(message);
    }
}
