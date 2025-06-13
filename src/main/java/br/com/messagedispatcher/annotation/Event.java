package br.com.messagedispatcher.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static br.com.messagedispatcher.constants.MessageDispatcherConstants.HandlerType;

/**
 * <h3>
 * Anotação para marcar métodos que processam mensagens do tipo Event.</br>
 * Annotation to mark methods that handle Event messages.
 * </h3>
 * <p>
 * Eventos não possuem retorno, recebem uma notificação e executam alguma ação.
 * <p>
 * Representam fatos que já aconteceram no sistema
 * <p>
 * São imutáveis
 * <p>
 * São o resultado da execução de commands
 * <p>
 * Exemplo: ProductCreated, OrderUpdated, CustomerDeleted
 * <p>
 * Podem ser usados para sincronizar diferentes modelos de dados
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@MessageHandler(handlerType = HandlerType.EVENT)
public @interface Event {
}