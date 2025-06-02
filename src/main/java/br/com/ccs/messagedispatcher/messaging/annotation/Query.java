package br.com.ccs.messagedispatcher.messaging.annotation;

import br.com.ccs.messagedispatcher.messaging.MessageType;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Queries são executados e devem retornar um resultado.
 * <p>
 * Queries devem ser idempotentes, ou seja, podem ser executados mais de uma vez com o mesmo resultado.
 * <p>
 * Apenas recuperam dados do sistema (read operations)
 * <p>
 * Não causam mudanças de estado
 * <p>
 * São otimizadas para leitura
 * <p>
 * Exemplo: GetProducts, FindOrderById, ListCustomers
 */
@Retention(RetentionPolicy.RUNTIME)
@MessageHandler(type = MessageType.QUERY)
@Documented
public @interface Query {

    @AliasFor(annotation = MessageHandler.class)
    Class<?> kind() default Object.class;
}
