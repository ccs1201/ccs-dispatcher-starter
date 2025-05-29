package br.com.ccs.messagedispatcher.messaging.publisher;

public final class MessageDispatcherHeaders {
    private MessageDispatcherHeaders() {
    }

    private static final String HEADER_PREFIX = "x-message-dispatcher-";
    public static final String MESSAGE_KINDA = HEADER_PREFIX + "kinda";
    public static final String MESSAGE_TIMESTAMP = HEADER_PREFIX + "timestamp";
    public static final String MESSAGE_SOURCE = HEADER_PREFIX + "originService";
    public static final String TYPE_ID = HEADER_PREFIX + "typeid";
    public static final String RESPONSE_FROM = HEADER_PREFIX + "response-from";
    public static final String RESPONSE_TIME_STAMP = HEADER_PREFIX + "response-timestamp";
    public static final String HAS_ERROR = HEADER_PREFIX + "has-error";
    public static final String EXCEPTION_MESSAGE = HEADER_PREFIX + "exception-message";
    public static final String EXCEPTION_TYPE = HEADER_PREFIX + "exception-type";
    public static final String FAILED_AT = HEADER_PREFIX + "x-failed-at";
}
