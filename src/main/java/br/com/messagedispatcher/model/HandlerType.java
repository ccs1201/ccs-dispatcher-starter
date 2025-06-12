package br.com.messagedispatcher.model;

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