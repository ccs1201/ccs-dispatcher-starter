package br.com.ccs.messagedispatcher.messaging.model;

import br.com.ccs.messagedispatcher.util.EnvironmentUtils;

import java.util.Arrays;

public record MessageDispatcherErrorResponse(String message, String cause, String originService) {

    public static MessageDispatcherErrorResponse of(Throwable exception) {
        return new MessageDispatcherErrorResponse(exception.getMessage(), Arrays.toString(exception.getStackTrace()), EnvironmentUtils.getAppName());
    }
}
