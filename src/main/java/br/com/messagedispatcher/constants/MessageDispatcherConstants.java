package br.com.messagedispatcher.constants;

public final class MessageDispatcherConstants {
    private MessageDispatcherConstants() {
    }

    public enum Headers {
        HANDLER_TYPE("handler-type"),
        MESSAGE_TIMESTAMP("timestamp"),
        MESSAGE_SOURCE("remoteService"),
        BODY_TYPE("body-type"),
        RESPONSE_FROM("response-from"),
        RESPONSE_TIME_STAMP("response-timestamp"),
        EXCEPTION_MESSAGE("exception-message"),
        EXCEPTION_ROOT_CAUSE("exception-root-cause"),
        FAILED_AT("failed-at");

        private static final String HEADER_PREFIX = "x-message-dispatcher-";
        private final String headerName;

        Headers(String headerName) {
            this.headerName = HEADER_PREFIX + headerName;
        }

        public String getHeaderName() {
            return headerName;
        }

        @Override
        public String toString() {
            return headerName;
        }
    }

    /**
     * Enum que define os tipos de handlers suportados.
     * Enum that defines the supported handler types.
     */
    public enum HandlerType {
        COMMAND,
        QUERY,
        NOTIFICATION,
        EVENT
    }

    public enum Exchange {
        /**
         * Topic exchange.
         */
        TOPIC("topic"),
        /**
         * Direct Echange
         */
        DIRECT("direct"),
        /**
         * Fanout exchange.
         */
        FANOUT("fanout"),
        /**
         * Headers exchange.
         */
        HEADERS("headers"),
        /**
         * Consistent Hash exchange.
         */
        CONSISTENT_HASH("x-consistent-hash");

        private final String type;

        Exchange(String type) {
            this.type = type;
        }

        public String getType() {
            return type;
        }
    }
}
