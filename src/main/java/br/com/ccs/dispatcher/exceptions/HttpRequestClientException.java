package br.com.ccs.dispatcher.exceptions;

public class HttpRequestClientException extends DispatcherException {
    public HttpRequestClientException(String msg, Exception e) {
        super(msg, e);
    }
}
