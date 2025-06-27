package br.com.messagedispatcher.router;

import br.com.messagedispatcher.model.MessageDispatcherRemoteInvocationResult;

public interface MessageRouter {
    MessageDispatcherRemoteInvocationResult routeMessage(Object objectMessage);
}
