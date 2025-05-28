package br.com.ccs.messagedispatcher.exceptions.model;

public record MessageDispatcherErrorResponse(String cause, String message) {
}
