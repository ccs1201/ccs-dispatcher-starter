/*
 * Copyright 2024 Cleber Souza
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package br.com.ccs.dispatcher.model;


import org.springframework.http.HttpMethod;

import java.util.Map;

/**
 * Esta classe representa um wrapper de mensagem que contém o caminho, método, cabeçalhos e corpo de uma mensagem.
 * É utilizada para encapsular a mensagem antes de enviá-la ao despachante de mensagens.
 * <p>
 * This class represents a message wrapper that contains the path, method, headers, and body of a message.
 * It is used to wrap the message before sending it to the message dispatcher.
 *
 * @author Cleber Souza
 * @version 1.0
 * @since 09/05/2025
 */

@SuppressWarnings("unused")
public class MessageWrapper {
    private String path;
    private String method;
    private Map<String, String> headers;
    private Object body;

    public MessageWrapper() {
    }

    public MessageWrapper(String path, String method, Map<String, String> headers, Object body) {
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
