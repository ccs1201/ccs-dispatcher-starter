package br.com.ccs.messagedispatcher.router;

public interface Endpoint {

    <I, R> R handle(I input);
}
