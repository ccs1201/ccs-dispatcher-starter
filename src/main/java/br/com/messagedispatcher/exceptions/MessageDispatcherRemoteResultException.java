package br.com.messagedispatcher.exceptions;

import br.com.messagedispatcher.model.MessageDispatcherRemoteInvocationResult;

public class MessageDispatcherRemoteResultException extends MessageDispatcherRemoteProcessException {

    private final String remoteExceptionType;

    public MessageDispatcherRemoteResultException(MessageDispatcherRemoteInvocationResult result) {
        super(result.exception(), result.remoteService());
        this.remoteExceptionType = result.exceptionType();
    }

    public String getRemoteExceptionType() {
        return remoteExceptionType;
    }
}
