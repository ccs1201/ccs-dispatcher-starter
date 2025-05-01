
package br.com.ccs.dispatcher.model;


import org.springframework.http.HttpMethod;

import java.util.Map;

public class MessageWrapper {
    private String path;
    private String method;
    private Map<String, String> headers;
    private Object body;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public void setMethod(HttpMethod method) {
        this.method = method.name();
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    public Object getBody() {
        return body;
    }

    public void setBody(Object body) {
        this.body = body;
    }
}
