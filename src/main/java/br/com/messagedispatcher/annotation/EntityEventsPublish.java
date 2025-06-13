package br.com.messagedispatcher.annotation;

import br.com.messagedispatcher.listener.MessageDispatcherEntityEventsListenerImpl;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Anotação para marcar entidades que devem ser monitoradas pelo {@link MessageDispatcherEntityEventsListenerImpl}.
 * Entidades anotadas com esta anotação terão Eventos de criação e atualização publicados para o tópico de mensagens.
 * <p>
 * <blockquote><pre>
 * Exemplo:
 * {@snippet java:
 * @Entity
 * @EntityEventsPublish
 *  public class Pessoa(){
 *
 *     @id
 *     private Long id;
 *      ....
 *  }
 *}
 * </pre></blockquote>
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface EntityEventsPublish {

    boolean publishCreate() default true;

    boolean publishUpdate() default true;
}
