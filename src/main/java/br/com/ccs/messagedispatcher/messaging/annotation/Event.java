package br.com.ccs.messagedispatcher.messaging.annotation;

import br.com.ccs.messagedispatcher.messaging.MessageType;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;


/**
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
@Retention(RetentionPolicy.RUNTIME)
@MessageHandler(action = MessageType.EVENT)
@Documented
public @interface Event {

    @AliasFor(annotation = MessageHandler.class)
    String type() default "";
}
