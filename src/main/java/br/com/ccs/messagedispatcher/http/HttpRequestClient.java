package br.com.ccs.messagedispatcher.http;

import br.com.ccs.messagedispatcher.messaging.model.MessageWrapper;

public interface HttpRequestClient {

    Object doRequest(MessageWrapper messageWrapper);
}
