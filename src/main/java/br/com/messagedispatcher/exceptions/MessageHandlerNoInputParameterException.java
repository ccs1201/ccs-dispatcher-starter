package br.com.messagedispatcher.exceptions;

public class MessageHandlerNoInputParameterException extends MessageDispatcherBeanResolutionException {
    public MessageHandlerNoInputParameterException(String message) {
        super(message);
    }
}
