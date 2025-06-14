package br.com.messagedispatcher.util.context;

import java.util.Collections;
import java.util.Map;

/**
 * Classe responsável por armazenar e gerenciar headers de mensagens em um contexto ThreadLocal.
 * Permite compartilhar informações entre diferentes partes da aplicação de forma thread-safe.
 * <p>
 * Class responsible for storing and managing message headers in a ThreadLocal context.
 * Allows sharing information between different parts of the application in a thread-safe way.
 */
public class MessageDispatcherContextHolder {

    /**
     * ThreadLocal que armazena um Map com os headers.
     * ThreadLocal that stores a Map with the headers.
     */
    private static final ThreadLocal<Map<String, Object>> context = new ThreadLocal<>();

    /**
     * Define os headers no contexto atual.
     * Sets the headers in the current context.
     *
     * @param headers Map contendo os headers a serem armazenados / Map containing the headers to be stored
     */
    public static void setHeaders(Map<String, Object> headers) {
        context.set(headers != null ? headers : Collections.emptyMap());
    }

    /**
     * Retorna todos os headers do contexto atual.
     * Returns all headers from the current context.
     *
     * @return Map com os headers armazenados / Map with stored headers
     */
    public static Map<String, Object> getHeaders() {
        return context.get();
    }

    /**
     * Retorna um header específico do contexto atual.
     * Returns a specific header from the current context.
     *
     * @param key Chave do header desejado / Key of the desired header
     * @return Valor do header como String / Header value as String
     */
    @SuppressWarnings("unused")
    public static String getHeader(String key) {
        return (String) context.get().get(key);
    }

    /**
     * Limpa todos os headers do contexto atual.
     * Clears all headers from the current context.
     */
    public static void clear() {
        context.remove();
    }
}
