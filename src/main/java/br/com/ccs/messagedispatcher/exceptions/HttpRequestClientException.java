package br.com.ccs.messagedispatcher.exceptions;

public class HttpRequestClientException extends MessageDispatcherRuntimeException {
    public HttpRequestClientException(String msg, Exception e) {
        super(msg, e);
    }
}
