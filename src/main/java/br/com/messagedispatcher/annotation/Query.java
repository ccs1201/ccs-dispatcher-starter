package br.com.messagedispatcher.annotation;

import br.com.messagedispatcher.model.HandlerType;

import java.lang.annotation.*;

/**
 * Anotação para marcar métodos que processam mensagens do tipo Query.
 * Annotation to mark methods that handle Query messages.
 */
@Target({ElementType.METHOD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@MessageHandler(handlerType = HandlerType.QUERY)
public @interface Query {
}