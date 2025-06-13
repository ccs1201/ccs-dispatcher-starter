package br.com.messagedispatcher.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static br.com.messagedispatcher.constants.MessageDispatcherConstants.HandlerType;

/**
 * <h3>
 * Anotação para marcar métodos que processam mensagens do tipo Query.</br>
 * Annotation to mark methods that handle Query messages.
 * </h3>
 * <p>
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
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@MessageHandler(handlerType = HandlerType.QUERY)
public @interface Query {
}