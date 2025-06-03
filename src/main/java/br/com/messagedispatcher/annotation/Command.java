package br.com.messagedispatcher.annotation;

import br.com.messagedispatcher.model.MessageType;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
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
@Retention(RetentionPolicy.RUNTIME)
@MessageHandler(type = MessageType.COMMAND)
@Documented
public @interface Command {

    @AliasFor(annotation = MessageHandler.class)
    Class<?> kind() default Object.class;
}
