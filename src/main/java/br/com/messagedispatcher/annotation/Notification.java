package br.com.messagedispatcher.annotation;

import br.com.messagedispatcher.model.MessageType;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Notificações são mensagens que avisam (notificam) sistemas sobre eventos que ocorreram
 * em outros sistemas.
 * <p>
 * Notificações são intra-domínio, ou seja, são publicadas no mesmo domínio (exchange)
 * que a aplicação pertence.
 * <p>
 * Informam sobre mudanças no sistema
 * <p>
 * Não carregam dados completos, apenas referências
 * <p>
 * Usadas para comunicação entre diferentes partes do sistema
 * <p>
 * Exemplo: OrderShipped, PaymentReceived
 */
@Retention(RetentionPolicy.RUNTIME)
@MessageHandler(type = MessageType.NOTIFICATION)
@Documented
public @interface Notification {

    @AliasFor(annotation = MessageHandler.class)
    Class<?> kind() default Object.class;
}
