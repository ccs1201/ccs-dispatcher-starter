package br.com.ccs.dispatcher.http;

import br.com.ccs.dispatcher.messaging.model.MessageWrapper;

public interface HttpRequestClient {

    Object doRequest(MessageWrapper messageWrapper);
}
