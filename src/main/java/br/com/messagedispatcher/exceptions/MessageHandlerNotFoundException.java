package br.com.messagedispatcher.exceptions;

/**
 * Exceção lançada quando não é encontrado um handler para processar um tipo de mensagem.
 * Exception thrown when no handler is found to process a message type.
 */
public class MessageHandlerNotFoundException extends RuntimeException {
    public MessageHandlerNotFoundException(String message) {
        super(message);
    }
}