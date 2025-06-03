package br.com.messagedispatcher.model;

public record MessageWrapperResponse(boolean hasError, Object data) {

    public static MessageWrapperResponse withSuccess(Object data) {
        return new MessageWrapperResponse(false, data);
    }

    public static MessageWrapperResponse withError(Object data) {
        return new MessageWrapperResponse(true, data);
    }
}
