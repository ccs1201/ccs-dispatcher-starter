package br.com.ccs.messagedispatcher.exceptions;

public class MessageHandlerWithoutInputParameterException extends MessageDispatcherBeanResolutionException {
    public MessageHandlerWithoutInputParameterException(String message) {
        super(message);
    }
}
