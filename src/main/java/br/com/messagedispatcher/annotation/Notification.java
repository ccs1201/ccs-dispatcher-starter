package br.com.messagedispatcher.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static br.com.messagedispatcher.constants.MessageDispatcherConstants.HandlerType;

/**
 * <h3>
 * Anotação para marcar métodos que processam mensagens do tipo Notification.</br>
 * Annotation to mark methods that handle Notification messages.
 * </h3>
 * <p>
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
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@MessageHandler(handlerType = HandlerType.NOTIFICATION)
public @interface Notification {
}