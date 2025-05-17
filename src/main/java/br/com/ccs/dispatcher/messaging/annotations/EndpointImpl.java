package br.com.ccs.dispatcher.messaging.annotations;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface EndpointImpl {
    Class<?> forClass();
}
