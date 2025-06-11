package br.com.messagedispatcher.exceptions;

/**
 * Exceção lançada quando um handler possui mais de um parâmetro de entrada.
 * Exception thrown when a handler has more than one input parameter.
 */
public class MessageHandlerMultipleInputParametersException extends RuntimeException {
    public MessageHandlerMultipleInputParametersException(String message) {
        super(message);
    }
}