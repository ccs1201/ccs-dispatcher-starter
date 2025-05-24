package br.com.ccs.messagedispatcher.messaging.exceptions;

public class MessagePublishExceptionMessage extends MessageDispatcherException {
    public MessagePublishExceptionMessage(String msg, Exception e) {
        super(msg, e);
    }
}
