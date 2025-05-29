package br.com.ccs.messagedispatcher.exceptions;

public class MessageHandlerDuplicatedInputParameterException extends MessageDispatcherBeanResolutionException {
    public MessageHandlerDuplicatedInputParameterException(String message) {
        super(message);
    }
}
