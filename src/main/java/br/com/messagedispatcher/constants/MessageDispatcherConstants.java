package br.com.messagedispatcher.constants;

public final class MessageDispatcherConstants {
    private MessageDispatcherConstants() {
    }

    public static class MessageDispatcherHeaders {
        private MessageDispatcherHeaders() {
        }

        private static final String HEADER_PREFIX = "x-message-dispatcher-";
        public static final String HANDLER_TYPE_HEADER = HEADER_PREFIX + "handler-type";
        public static final String MESSAGE_TIMESTAMP_HEADER = HEADER_PREFIX + "timestamp";
        public static final String MESSAGE_SOURCE_HEADER = HEADER_PREFIX + "remoteService";
        public static final String BODY_TYPE_HEADER = HEADER_PREFIX + "body-type";
        public static final String RESPONSE_FROM_HEADER = HEADER_PREFIX + "response-from";
        public static final String RESPONSE_TIME_STAMP_HEADER = HEADER_PREFIX + "response-timestamp";
        public static final String EXCEPTION_MESSAGE_HEADER = HEADER_PREFIX + "exception-message";
        public static final String EXCEPTION_ROOT_CAUSE_HEADER = HEADER_PREFIX + "exception-root-cause";
        public static final String FAILED_AT_HEADER = HEADER_PREFIX + "failed-at";
    }
}
