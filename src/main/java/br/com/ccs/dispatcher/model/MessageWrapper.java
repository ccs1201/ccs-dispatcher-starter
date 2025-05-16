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

import br.com.ccs.dispatcher.util.httpservlet.RequestContextUtil;
import br.com.ccs.dispatcher.util.validator.BeanValidatorUtil;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.HttpMethod;

import java.util.HashMap;
import java.util.Map;

/**
 * Esta classe representa um wrapper de mensagem que contém o caminho, método, cabeçalhos, queryParams e corpo de uma mensagem.
 * É utilizada para encapsular a mensagem antes de enviá-la ao despachante de mensagens.
 * <p>
 * This class represents a message wrapper that contains the path, method, headers, queryParams and body of a message.
 * It is used to wrap the message before sending it to the message dispatcher.
 *
 * @author Cleber Souza
 * @version 1.0
 * @since 09/05/2025
 */

@SuppressWarnings("unused")
public class MessageWrapper {
    @NotBlank(message = "must not be null or empty")
    private String path;
    @NotBlank(message = "method must not be null or empty")
    private String method;
    @NotNull(message = "must not be null")
    private Map<String, String> headers;
    private Map<String, String> queryParams;
    private String body;

    public MessageWrapper() {
    }

    public MessageWrapper(String path,
                          String method,
                          Map<String, String> headers,
                          Map<String, String> queryParams,
                          String body) {
        this.path = path;
        this.method = method;
        this.headers = headers;
        this.queryParams = queryParams;
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

    public String getBody() {
        return body;
    }

    public Map<String, String> getQueryParams() {
        return queryParams;
    }

    public static class MessageWrapperBuilder {

        private String path;
        private String method;
        private Map<String, String> headers;
        Map<String, String> queryParams;
        private String body;

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

        public MessageWrapperBuilder header(String key, String value) {
            if (this.headers == null) {
                this.headers = new HashMap<>();
            }
            this.headers.put(key, value);
            return this;
        }


        public MessageWrapperBuilder autoSetHeaders() {
            return headers(RequestContextUtil.getHeaders());
        }

        public MessageWrapperBuilder queryParams(Map<String, String> queryParams) {
            this.queryParams = queryParams;
            return this;
        }

        public MessageWrapperBuilder queryParams(String key, String Param) {
            if (this.queryParams == null) {
                this.queryParams = new HashMap<>();
            }
            this.queryParams.put(key, Param);
            return this;
        }

        public MessageWrapperBuilder autoSetQueryParams() {
            return queryParams(RequestContextUtil.getQueryParams());
        }

        public MessageWrapperBuilder body(String body) {
            this.body = body;
            return this;
        }

        public MessageWrapper build() {
            var messageWrapper = new MessageWrapper(path, method, headers, queryParams, body);
            BeanValidatorUtil.validate(messageWrapper);
            return messageWrapper;
        }
    }

    @Override
    public String toString() {
        return "MessageWrapper{" +
                "path='" + path + '\'' +
                ", method='" + method + '\'' +
                ", headers=" + headers +
                ", queryParams=" + queryParams +
                ", body='" + body + '\'' +
                '}';
    }
}
