package br.com.ccs.messagedispatcher.messaging.annotation;

import br.com.ccs.messagedispatcher.messaging.MessageKinda;
import org.springframework.core.annotation.AliasFor;

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
     * Ação que o método processa (command, query, notification or event).
     * Action that's handler process (command, query, notification or event).
     */
    MessageKinda kinda();

    /**
     * Tipo de Payload que este handler processa
     * Type of Payload that's handler process.
     */
    @AliasFor("forClass")
    String type() default "";
}
