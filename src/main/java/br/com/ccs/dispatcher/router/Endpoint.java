package br.com.ccs.dispatcher.router;

public interface Endpoint {

    <I, R> R handle(I input);
}
