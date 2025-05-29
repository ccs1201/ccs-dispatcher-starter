package br.com.ccs.messagedispatcher.messaging.model;

public record MessageWrapperResponse(boolean hasError, Object data) {
}
