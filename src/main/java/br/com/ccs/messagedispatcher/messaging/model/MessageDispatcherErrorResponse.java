package br.com.ccs.messagedispatcher.messaging.model;

import java.util.Arrays;

public record MessageDispatcherErrorResponse(String message, String cause) {

    public static MessageDispatcherErrorResponse of(Exception exception) {
        return new MessageDispatcherErrorResponse(exception.getMessage(), Arrays.toString(exception.getStackTrace()));
    }
}
