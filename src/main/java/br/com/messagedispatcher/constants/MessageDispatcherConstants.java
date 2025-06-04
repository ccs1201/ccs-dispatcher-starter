package br.com.messagedispatcher.constants;

public final class MessageDispatcherConstants {
    private MessageDispatcherConstants() {
    }

    public static class MessageDispatcherHeaders {
        private MessageDispatcherHeaders() {
        }

        private static final String HEADER_PREFIX = "x-message-dispatcher-";
        public static final String MESSAGE_TYPE = HEADER_PREFIX + "message-type";
        public static final String MESSAGE_TIMESTAMP = HEADER_PREFIX + "timestamp";
        public static final String MESSAGE_SOURCE = HEADER_PREFIX + "remoteService";
        public static final String BODY_TYPE = HEADER_PREFIX + "body-type";
        public static final String RESPONSE_FROM = HEADER_PREFIX + "response-from";
        public static final String RESPONSE_TIME_STAMP = HEADER_PREFIX + "response-timestamp";
        public static final String EXCEPTION_MESSAGE = HEADER_PREFIX + "exception-message";
        public static final String EXCEPTION_ROOT_CAUSE = HEADER_PREFIX + "exception-root-cause";
        public static final String FAILED_AT = HEADER_PREFIX + "failed-at";
    }
}
