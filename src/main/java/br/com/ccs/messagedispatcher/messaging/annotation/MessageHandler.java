package br.com.ccs.messagedispatcher.messaging.annotation;

import br.com.ccs.messagedispatcher.messaging.MessageAction;

import java.lang.annotation.*;

/**
 * Anotação para marcar os métodos que processam mensagens.
 * Annotation to mark methods that's handle a message.
 */

@Target({ElementType.METHOD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MessageHandler {

    /**
     * Ação que o método processa (command, query or event).
     * Action that's handler process (command, query or event).
     */
    MessageAction action();

    /**
     * Tipo de mensagem que este handler processa
     * Type of message That's handler process.
     */
    String type() default "";

    /**
     * Classe que o handler processa.
     * Class that's handler process.
     */
    Class<?> forClass() default Void.class;
}
