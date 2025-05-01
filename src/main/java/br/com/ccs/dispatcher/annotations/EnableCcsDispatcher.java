
package br.com.ccs.dispatcher.annotations;

import br.com.ccs.dispatcher.config.CcsDispatcherAutoConfiguration;
import org.springframework.context.annotation.Import;
import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(CcsDispatcherAutoConfiguration.class)
public @interface EnableCcsDispatcher {
}
