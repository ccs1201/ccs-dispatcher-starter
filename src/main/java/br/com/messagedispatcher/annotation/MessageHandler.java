package br.com.messagedispatcher.annotation;

import br.com.messagedispatcher.constants.MessageDispatcherConstants;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Anotação para marcar os métodos que processam mensagens.
 * Annotation to mark methods that's handle a message.
 */

@Target({ElementType.METHOD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MessageHandler {

    /**
     * Tipo de mensagem que o método processa (command, query, notification ou event).
     * Type of message that's handler process (command, query, notification or event).
     */
    MessageDispatcherConstants.HandlerType handlerType();

    /**
     * Tipo de Payload que este handler processa
     * Type of Payload that's handler process.
     */
    Class<?> kind() default Object.class;
}
