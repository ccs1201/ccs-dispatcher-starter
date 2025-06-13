package br.com.messagedispatcher.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static br.com.messagedispatcher.constants.MessageDispatcherConstants.HandlerType;

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