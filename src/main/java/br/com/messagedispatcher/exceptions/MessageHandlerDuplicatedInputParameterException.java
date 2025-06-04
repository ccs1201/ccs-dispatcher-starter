package br.com.messagedispatcher.exceptions;

public class MessageHandlerDuplicatedInputParameterException extends MessageDispatcherBeanResolutionException {
    public MessageHandlerDuplicatedInputParameterException(String message) {
        super(message);
    }
}
