package br.com.ccs.messagedispatcher.messaging.publisher;

public final class MessageDispatcherHeaders {
    private MessageDispatcherHeaders() {
    }

    public static final String HEADER_PREFIX = "x-message-dispatcher-";
    public static final String HEADER_MESSAGE_ACTION = HEADER_PREFIX + "message-action";
    public static final String HEADER_MESSAGE_TIMESTAMP = HEADER_PREFIX + "message-timestamp";
    public static final String HEADER_MESSAGE_SOURCE = HEADER_PREFIX + "message-source";
    public static final String HEADER_TYPE_ID = HEADER_PREFIX + "typeid";
}
