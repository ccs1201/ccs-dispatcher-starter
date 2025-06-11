package br.com.messagedispatcher.annotation;

import br.com.messagedispatcher.model.HandlerType;

import java.lang.annotation.*;

/**
 * Anotação para marcar métodos que processam mensagens do tipo Command.
 * Annotation to mark methods that handle Command messages.
 */
@Target({ElementType.METHOD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@MessageHandler(handlerType = HandlerType.COMMAND)
public @interface Command {
}