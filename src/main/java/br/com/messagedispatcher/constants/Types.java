package br.com.messagedispatcher.constants;

public class Types {

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
