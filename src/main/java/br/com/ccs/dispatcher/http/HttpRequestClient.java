package br.com.ccs.dispatcher.http;

import br.com.ccs.dispatcher.model.MessageWrapper;

public interface HttpRequestClient {

    Object doRequest(MessageWrapper messageWrapper);
}
