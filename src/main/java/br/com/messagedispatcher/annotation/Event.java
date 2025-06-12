package br.com.messagedispatcher.annotation;

import br.com.messagedispatcher.model.HandlerType;

import java.lang.annotation.*;

/**
 * Anotação para marcar métodos que processam mensagens do tipo Event.
 * Annotation to mark methods that handle Event messages.
 */
@Target({ElementType.METHOD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@MessageHandler(handlerType = HandlerType.EVENT)
public @interface Event {
}