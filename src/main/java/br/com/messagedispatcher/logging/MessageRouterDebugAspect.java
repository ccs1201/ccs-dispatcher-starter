package br.com.messagedispatcher.logging;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Aspect
@Component
@ConditionalOnProperty(value = "message.dispatcher.logging.messagerouter.logging-enable", havingValue = "true")
public class MessageRouterDebugAspect {

    private static final Logger log = LoggerFactory.getLogger(MessageRouterDebugAspect.class);
    private final ObjectMapper mapper;
    private final MessageConverter messageConverter;

    public MessageRouterDebugAspect(ObjectMapper objectMapper, MessageConverter messageConverter) {
        this.mapper = objectMapper;
        this.messageConverter = messageConverter;
        log.warn("\n\n########### Message Dispatcher com modo DEBUG ativo ###########\n");
        log.info("MessageRouterDebugAspect inicializado e ativo, as mensagens roteadas no log de depuração serão exibidas");
    }

    @Pointcut("execution(* br.com.messagedispatcher.router.MessageRouter.routeMessage(..))")
    public void routeMethod() {
    }

    @Before("routeMethod()")
    public void logCallerIfDebug(JoinPoint joinPoint) {

        var caller = Optional.of(joinPoint.getArgs()[0]);

        caller.ifPresent(c -> {
            if (caller.get() instanceof org.springframework.amqp.core.Message message) {
                try {
                    log.debug("""
                                    
                                        Mensagem recebida por: {}.{}
                                        Exchange: {}
                                        RoutingKey: {}
                                        Headers: {}
                                        Body: {}
                                    """,
                            message.getMessageProperties().getTargetMethod().getDeclaringClass().getName(),
                            message.getMessageProperties().getTargetMethod().getName(),
                            message.getMessageProperties().getReceivedExchange(),
                            message.getMessageProperties().getReceivedRoutingKey(),
                            message.getMessageProperties().getHeaders(),
                            mapper.writeValueAsString(messageConverter.fromMessage(message)));
                } catch (Exception e) {
                    log.error("Falha ao capturar informações de origem da mensagem em modo debug", e);
                }
            }
        });
    }
}

