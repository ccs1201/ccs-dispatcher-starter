package br.com.ccs.dispatcher.model;


import org.springframework.http.HttpMethod;

import java.util.Map;

public class MessageWrapper {
    private String path;
    private String method;
    private Map<String, String> headers;
    private Object body;

    MessageWrapper(String path, String method, Map<String, String> headers, Object body) {
        this.path = path;
        this.method = method;
        this.headers = headers;
        this.body = body;
    }

    public static MessageWrapperBuilder builder() {
        return new MessageWrapperBuilder();
    }

    public String getPath() {
        return path;
    }

    public String getMethod() {
        return method;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public Object getBody() {
        return body;
    }

    public static class MessageWrapperBuilder {
        private String path;
        private String method;
        private Map<String, String> headers;
        private Object body;

        MessageWrapperBuilder() {
        }

        public MessageWrapperBuilder path(String path) {
            this.path = path;
            return this;
        }

        public MessageWrapperBuilder method(String method) {
            this.method = method;
            return this;
        }

        public MessageWrapperBuilder method(HttpMethod method) {
            this.method = method.name();
            return this;
        }

        public MessageWrapperBuilder headers(Map<String, String> headers) {
            this.headers = headers;
            return this;
        }

        public MessageWrapperBuilder body(Object body) {
            this.body = body;
            return this;
        }

        public MessageWrapper build() {
            return new MessageWrapper(path, method, headers, body);
        }
    }
}
