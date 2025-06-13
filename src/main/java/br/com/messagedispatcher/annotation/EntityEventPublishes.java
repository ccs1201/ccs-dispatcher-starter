package br.com.messagedispatcher.annotation;

import br.com.messagedispatcher.listener.MessageDispatcherEntityListener;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Anotação para marcar entidades que devem ser monitoradas pelo {@link MessageDispatcherEntityListener}.
 * Para publicar eventos de entidade.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface EntityEventPublishes {

    boolean publishCreate() default true;
    boolean publishUpdate() default true;
}
