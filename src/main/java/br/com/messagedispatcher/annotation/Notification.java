package br.com.messagedispatcher.annotation;

import br.com.messagedispatcher.model.HandlerType;

import java.lang.annotation.*;

/**
 * Anotação para marcar métodos que processam mensagens do tipo Notification.
 * Annotation to mark methods that handle Notification messages.
 */
@Target({ElementType.METHOD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@MessageHandler(handlerType = HandlerType.NOTIFICATION)
public @interface Notification {
}