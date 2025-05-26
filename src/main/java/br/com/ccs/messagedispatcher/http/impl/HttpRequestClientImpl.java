/*
 * Copyright 2025 Cleber Souza
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

package br.com.ccs.messagedispatcher.http.impl;

import br.com.ccs.messagedispatcher.MessageDispatcherListener;
import br.com.ccs.messagedispatcher.exceptions.HttpRequestClientException;
import br.com.ccs.messagedispatcher.http.HttpRequestClient;
import br.com.ccs.messagedispatcher.messaging.model.MessageWrapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.context.WebServerInitializedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Implementação do {@link HttpRequestClient} que utiliza a API de
 * {@link HttpClient} para fazer as requisições HTTP.
 * Primariamente utilizada para encaminhar as mensagens recebidas no {@link MessageDispatcherListener}
 * para servidor web da aplicação.
 * <p>
 * Implementation of {@link HttpRequestClient} that uses the
 * {@link HttpClient} API to make HTTP requests.
 * Primarily used to forward the messages received in {@link MessageDispatcherListener}
 * to the web server of the application.
 *
 * @author Cleber Souza
 * @version 1.0
 * @since 10/05/2025
 */

//todo verificar se ainda será necessário

@Component
public class HttpRequestClientImpl implements HttpRequestClient, ApplicationListener<WebServerInitializedEvent> {

    private final List<String> headersToIgnore = List.of("host", "content-length", "connection");

    private final Logger log = LoggerFactory.getLogger(HttpRequestClientImpl.class);
    private URI BASE_URL;
    private final ObjectMapper objectMapper;

    public HttpRequestClientImpl(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void onApplicationEvent(WebServerInitializedEvent event) {
        log.warn("!!!!!Talvez não seja mais necessário.............");
        this.BASE_URL = URI.create("http://localhost:" + event.getWebServer().getPort());
        log.debug("HttpRequestClient inicializado com URL Base: " + BASE_URL);
    }

    /**
     * Faz a requisição HTTP utilizando a API de {@link HttpClient}
     * <p>
     * Makes an HTTP request using the {@link HttpClient} API
     *
     * @param messageWrapper Mensagem recebida no dispatcher
     * @return Object - {@link HttpResponse} body
     */
    public Object doRequest(MessageWrapper messageWrapper) {
        var httpRequest = HttpRequest.newBuilder()
                .timeout(Duration.ofSeconds(15));

        addUri(messageWrapper, httpRequest);
        addHeaders(messageWrapper, httpRequest);
        addParams(messageWrapper, httpRequest);

        try (var httpClient = HttpClient.newBuilder().version(HttpClient.Version.HTTP_1_1).build()) {
            addMethodAndBody(messageWrapper, httpRequest);
            var request = httpRequest.build();
            var response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            return response.body();
        } catch (InterruptedException | IOException e) {
            throw new HttpRequestClientException("Erro ao encaminhar a mensagem recebida no message dispatcher para " +
                    "o Controller: " + messageWrapper.getPath(), e);
        }
    }

    private void addUri(MessageWrapper messageWrapper, HttpRequest.Builder builder) {
        if (messageWrapper.getPath().startsWith("/")) {
            builder.uri(BASE_URL.resolve(messageWrapper.getPath()));
            return;
        }

        builder.uri(BASE_URL.resolve("/".concat(messageWrapper.getPath())));
    }

    private void addMethodAndBody(MessageWrapper messageWrapper, HttpRequest.Builder builder) throws JsonProcessingException {
        if (messageWrapper.getBody() != null) {
            builder.method(messageWrapper.getMethod(), HttpRequest
                    .BodyPublishers
                    .ofString(objectMapper.writeValueAsString(messageWrapper.getBody())));
            return;
        }

        builder.method(messageWrapper.getMethod(), HttpRequest.BodyPublishers.noBody());
    }

    private void addParams(MessageWrapper messageWrapper, HttpRequest.Builder builder) {
        if (messageWrapper.getQueryParams() != null) {
            String queryParams = messageWrapper
                    .getQueryParams()
                    .entrySet()
                    .stream()
                    .map(entry -> entry.getKey().concat("=").concat(entry.getValue()))
                    .reduce((s, s2) -> s.concat("&").concat(s2)).orElseGet(() -> "");

            builder.uri(BASE_URL.resolve("?".concat(queryParams)));
        }
    }

    private void addHeaders(MessageWrapper messageWrapper, HttpRequest.Builder builder) {
        if (messageWrapper.getHeaders() != null) {
            var headers = sanitiseHeaders(messageWrapper.getHeaders());
            headers.keySet()
                    .forEach(k -> builder.header(k, messageWrapper.getHeaders().get(k)));
        }
    }

    private Map<String, String> sanitiseHeaders(Map<String, String> headers) {
        var newHeaders = new HashMap<>(headers);
        headersToIgnore.forEach(newHeaders::remove);
        return newHeaders;
    }
}
