package br.com.ccs.dispatcher.messaging.annotation;

import br.com.ccs.dispatcher.messaging.MessageType;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.Documented;

/**
 * Notificações são mensagens que avisam (notifica) sistemas sobre eventos que ocorreram
 * em outros sistemas.
 * <p>
 * Informam sobre mudanças no sistema
 * <p>
 * Não carregam dados completos, apenas referências
 * <p>
 * Usadas para comunicação entre diferentes partes do sistema
 * <p>
 * Exemplo: OrderShipped, PaymentReceived
 */
@MessageHandler(action = MessageType.NOTIFICATION)
@Documented
public @interface Notification {

    @AliasFor(annotation = MessageHandler.class)
    String type();
}
