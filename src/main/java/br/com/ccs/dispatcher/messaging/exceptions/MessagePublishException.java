package br.com.ccs.dispatcher.messaging.exceptions;

public class MessagePublishException extends DispatcherException {
    public MessagePublishException(String msg, Exception e) {
        super(msg, e);
    }
}
