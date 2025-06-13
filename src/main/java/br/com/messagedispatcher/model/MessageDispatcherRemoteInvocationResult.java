package br.com.messagedispatcher.model;

import br.com.messagedispatcher.util.MessageDispatcherUtils;
import org.springframework.lang.Nullable;

public record MessageDispatcherRemoteInvocationResult(Object value,
                                                      Throwable exception,
                                                      String exceptionType,
                                                      String remoteService) {

    public static MessageDispatcherRemoteInvocationResult of(Throwable exception) {
        return new MessageDispatcherRemoteInvocationResult(null, exception, exception.getClass().getSimpleName(), MessageDispatcherUtils.getAppName());
    }

    public static MessageDispatcherRemoteInvocationResult of(@Nullable Object value) {
        return new MessageDispatcherRemoteInvocationResult(value, null, null, MessageDispatcherUtils.getAppName());
    }

    public boolean hasException() {
        return this.exception() != null;
    }
}
