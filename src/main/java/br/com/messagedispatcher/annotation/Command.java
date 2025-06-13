package br.com.messagedispatcher.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static br.com.messagedispatcher.constants.MessageDispatcherConstants.HandlerType;

/**
 * <h3>
 * Anotação para marcar métodos que processam mensagens do tipo Command.</br>
 * Annotation to mark methods that handle Command messages.
 * </h3>
 * <p>
 * Commandos são executados e podem ou não retornar um resultado.
 * <p>
 * Commandos devem ser idempotentes, ou seja, podem ser executados mais de uma vez com o mesmo resultado.
 * <p>
 * Representam uma intenção de mudança no sistema
 * <p>
 * Alteram o estado do sistema (write operations)
 * <p>
 * Exemplo: CreateProduct, UpdateOrder, DeleteCustomer
 * <p>
 * Devem ser validados antes de serem executados
 * <p>
 * Podem ser rejeitados, gerar erro ou produzir eventos
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@MessageHandler(handlerType = HandlerType.COMMAND)
public @interface Command {
}