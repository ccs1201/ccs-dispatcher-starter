package br.com.ccs.dispatcher.messaging.annotation;

import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.Documented;


/**
 * Eventos não possuem retorno, recebem um notifição e executam alguma ação.
 * <p>
 * Eventos são publicados e consumidos por todos os contextos.
 * <p>
 *
 */
@MessageHandler(action = "Event")
@Documented
public @interface Event {

    @AliasFor(annotation = MessageHandler.class)
    String type();
}
