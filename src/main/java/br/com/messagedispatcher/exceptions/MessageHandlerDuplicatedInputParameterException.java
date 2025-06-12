package br.com.messagedispatcher.exceptions;

/**
 * Exceção lançada quando existem handlers duplicados para o mesmo tipo de entrada.
 * Exception thrown when there are duplicate handlers for the same input type.
 */
public class MessageHandlerDuplicatedInputParameterException extends RuntimeException {
    public MessageHandlerDuplicatedInputParameterException(String message) {
        super(message);
    }
}