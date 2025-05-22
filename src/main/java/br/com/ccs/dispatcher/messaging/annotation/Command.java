package br.com.ccs.dispatcher.messaging.annotation;

import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.Documented;

/**
 * Commandos são publicados e consumidos por todos os contextos.
 * <p>
 * Commandos são executados e podem ou não retornar um resultado.
 * <p>
 * Commandos devem ser idempotentes, ou seja, podem ser executados mais de uma vez com o mesmo resultado.
 * <p>
 */
@MessageHandler(action = "Command")
@Documented
public @interface Command {

    @AliasFor(annotation = MessageHandler.class)
    String type();


}
