package br.com.messagedispatcher.exceptions;

/**
 * Exceção lançada quando um handler não possui parâmetro de entrada.
 * Exception thrown when a handler has no input parameter.
 */
public class MessageHandlerNoInputParameterException extends RuntimeException {
    public MessageHandlerNoInputParameterException(String message) {
        super(message);
    }
}