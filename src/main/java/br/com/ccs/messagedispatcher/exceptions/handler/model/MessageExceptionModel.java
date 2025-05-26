package br.com.ccs.messagedispatcher.exceptions.handler.model;

public record MessageExceptionModel(String message, Throwable cause) {
}
