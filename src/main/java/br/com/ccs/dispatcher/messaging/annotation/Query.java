package br.com.ccs.dispatcher.messaging.annotation;

import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.Documented;

/**
 * Queries são publicados e consumidos por todos os contextos.
 * <p>
 * Queries são executados e devem retornar um resultado.
 * <p>
 * Queries devem ser idempotentes, ou seja, podem ser executados mais de uma vez com o mesmo resultado.
 * <p>
 */
@MessageHandler(action = "Query")
@Documented
public @interface Query {

    @AliasFor(annotation = MessageHandler.class)
    String type();
}
