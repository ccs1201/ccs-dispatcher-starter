package br.com.ccs.messagedispatcher.exceptions;

import br.com.ccs.messagedispatcher.util.EnvironmentUtils;

public class MessageDispatcherRuntimeException extends RuntimeException {

    private static final String appName = EnvironmentUtils.getAppName();

    public MessageDispatcherRuntimeException(String message, Throwable cause) {
        super(appName + ": " + message, cause);
    }

    public MessageDispatcherRuntimeException(String message) {
        super(appName + ": " + message);
    }
}
