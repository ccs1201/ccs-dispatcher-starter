package br.com.ccs.dispatcher.messaging.annotation;

import br.com.ccs.dispatcher.messaging.MessageType;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.Documented;

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
@MessageHandler(action = MessageType.QUERY)
@Documented
public @interface Query {

    @AliasFor(annotation = MessageHandler.class)
    String type();
}
