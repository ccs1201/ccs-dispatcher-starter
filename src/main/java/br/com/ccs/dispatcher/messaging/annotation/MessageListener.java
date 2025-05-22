package br.com.ccs.dispatcher.messaging.annotation;

import org.springframework.stereotype.Component;

import java.lang.annotation.*;

/**
 * Anotação para marcar classes que contém métodos que processam mensagens.
 * Annotation to mark classes that contains methods that's handle a message.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Component
@Documented
public @interface MessageListener {
}
