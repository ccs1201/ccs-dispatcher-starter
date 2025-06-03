package br.com.messagedispatcher.exceptions;

public class MessagePublishException extends MessageDispatcherRuntimeException {
    public MessagePublishException(String msg, Exception e) {
        super(msg, e);
    }
}
