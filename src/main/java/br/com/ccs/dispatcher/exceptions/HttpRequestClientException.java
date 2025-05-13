package br.com.ccs.dispatcher.exceptions;

public class HttpRequestClientException extends RuntimeException {
    public HttpRequestClientException(String msg, Exception e) {
        super(msg, e);
    }
}
