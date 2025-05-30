package br.com.ccs.messagedispatcher.exceptions;

public class MessageHandlerNoInputParameterException extends MessageDispatcherBeanResolutionException {
    public MessageHandlerNoInputParameterException(String message) {
        super(message);
    }
}
