package br.com.ccs.messagedispatcher.messaging.publisher;

public final class MessageHeaders {
    private MessageHeaders() {
    }

    public static final String HEADER_PREFIX = "x-message-dispatcher-";
    public static final String HEADER_MESSAGE_TYPE = HEADER_PREFIX + "message-type";
    public static final String HEADER_MESSAGE_TIMESTAMP = HEADER_PREFIX + "message-timestamp";
    public static final String HEADER_MESSAGE_SOURCE = HEADER_PREFIX + "message-source";
    public static final String HEADER_MESSAGE_PATH = HEADER_PREFIX + "message-path";
    public static final String HEADER_MESSAGE_METHOD = HEADER_PREFIX + "message-method";
    public static final String HEADER_TYPE_ID = HEADER_PREFIX + "typeid";
}
