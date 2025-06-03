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

package br.com.messagedispatcher.util.httpservlet;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Classe utilitária para obter informações do objeto HttpServletRequest atual.
 * Utiliza o RequestContextHolder para acessar o objeto HttpServletRequest e obter informações como cabeçalhos, parâmetros de consulta, etc.
 * <p>
 * This class provides utility methods to retrieve information from the current HTTP request.
 * It uses the RequestContextHolder to access the current request and retrieve headers, query parameters, and other request-related information.
 *
 * @author Cleber Souza
 * @version 1.0
 * @since 10/05/2025
 */
public class RequestContextUtil {

    private RequestContextUtil() {
    }

    /**
     * Obtém o objeto HttpServletRequest atual.
     * Se o contexto de solicitação não estiver disponível, retorna null.
     * <p>
     * Retrieves the current HttpServletRequest object.
     * If the request context is not available, it returns null.
     *
     * @return HttpServletRequest
     * @see HttpServletRequest
     */
    public static HttpServletRequest getCurrentRequest() {
        return RequestContextHolder.getRequestAttributes() != null ?
                ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest() : null;
    }

    /**
     * Obtém o cabeçalho especificado do objeto HttpServletRequest atual.
     * Se o objeto HttpServletRequest não estiver disponível, retorna um Optional vazio.
     * <p>
     * Retrieves the specified header from the current HttpServletRequest object.
     * If the HttpServletRequest object is not available, it returns an empty Optional.
     *
     * @param headerName O nome do cabeçalho a ser obtido.
     * @return Optional<String> com o valor do cabeçalho ou empty se o cabeçalho não estiver presente.
     * @see HttpServletRequest
     */
    public static Optional<String> getHeader(String headerName) {

        var request = getCurrentRequest();

        if (request == null) {
            return Optional.empty();
        }

        return Optional.ofNullable(request.getHeader(headerName));
    }

    /**
     * Obtém todos os cabeçalhos do objeto HttpServletRequest atual.
     * Se o objeto HttpServletRequest não estiver disponível, retorna um mapa vazio.
     * <p>
     * Retrieves all headers from the current HttpServletRequest object.
     * If the HttpServletRequest object is not available, it returns an empty map.
     *
     * @return Map<String, String> Um mapa contendo todos os cabeçalhos.
     * @see HttpServletRequest
     */

    public static Map<String, String> getHeaders() {
        var request = getCurrentRequest();
        var headers = new HashMap<String, String>();

        if (request != null) {
            Collections.list(request.getHeaderNames())
                    .forEach(headerName -> headers.put(headerName, request.getHeader(headerName)));

            return headers;
        }

        return Map.of();
    }

    /**
     * Obtém todos os parâmetros de consulta do objeto HttpServletRequest atual.
     * Se o objeto HttpServletRequest não estiver disponível, retorna um mapa vazio.
     * <p>
     * Retrieves all query parameters from the current HttpServletRequest object.
     * If the HttpServletRequest object is not available, it returns an empty map.
     *
     * @return Map<String, String> Um mapa contendo os parâmetros de consulta.
     * @see HttpServletRequest
     */
    public static Map<String, String> getQueryParams() {
        var request = getCurrentRequest();
        var queryParams = new HashMap<String, String>();

        if (request != null) {
            Collections.list(request.getParameterNames())
                    .forEach(paramName -> queryParams.put(paramName, request.getParameter(paramName)));
            return queryParams;
        }

        return Map.of();
    }

    /**
     * Obtém o valor de um parâmetro de consulta específico do objeto HttpServletRequest atual.
     * Se o objeto HttpServletRequest não estiver disponível, retorna uma string vazia.
     * <p>
     * Retrieves the value of a specific query parameter from the current HttpServletRequest object.
     * If the HttpServletRequest object is not available, it returns an empty string.
     *
     * @param paramName O nome do parâmetro de consulta a ser obtido.
     * @return String O valor do parâmetro de consulta.
     * @see HttpServletRequest
     */
    public static String getQueryParam(String paramName) {
        var request = getCurrentRequest();

        if (request != null) {
            return request.getParameter(paramName);
        }

        return "";
    }

    /**
     * Obtém o método HTTP do objeto HttpServletRequest atual.
     * Se o objeto HttpServletRequest não estiver disponível, retorna uma string vazia.
     * <p>
     * Retrieves the HTTP method from the current HttpServletRequest object.
     * If the HttpServletRequest object is not available, it returns an empty string.
     *
     * @return String O método HTTP.
     * @see HttpServletRequest
     */
    public static String getMethod() {
        var request = getCurrentRequest();

        if (request != null) {
            return request.getMethod();
        }

        return "";
    }

    /**
     * Obtém o caminho da URL do objeto HttpServletRequest atual.
     * Se o objeto HttpServletRequest não estiver disponível, retorna uma string vazia.
     * <p>
     * Retrieves the URL path from the current HttpServletRequest object.
     * If the HttpServletRequest object is not available, it returns an empty string.
     *
     * @return String O caminho da URL.
     * @see HttpServletRequest
     */

    public static String getPath() {
        var request = getCurrentRequest();

        if (request != null) {
            return request.getRequestURI();
        }

        return "";
    }

}
