package br.com.messagedispatcher.model;

import br.com.messagedispatcher.util.EnvironmentUtils;
import org.springframework.lang.Nullable;

public record MessageDispatcherRemoteInvocationResult(Object value,
                                                      Throwable exception,
                                                      String remoteService) {

    public static MessageDispatcherRemoteInvocationResult of(Throwable exception) {
        return new MessageDispatcherRemoteInvocationResult(null, exception, EnvironmentUtils.getAppName());
    }

    public static MessageDispatcherRemoteInvocationResult of(@Nullable Object value) {
        return new MessageDispatcherRemoteInvocationResult(value, null, EnvironmentUtils.getAppName());
    }

    public boolean hasException() {
        return this.exception() != null;
    }
}
