package br.com.ccs.messagedispatcher.messaging.exceptions;

public class HttpRequestClientExceptionMessage extends MessageDispatcherException {
    public HttpRequestClientExceptionMessage(String msg, Exception e) {
        super(msg, e);
    }
}
