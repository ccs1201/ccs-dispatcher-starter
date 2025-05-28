package br.com.ccs.messagedispatcher.beandiscover.impl;

import br.com.ccs.messagedispatcher.beandiscover.MessageDispatcherAnnotatedMethodDiscover;
import br.com.ccs.messagedispatcher.exceptions.MessageHandlerDuplicatedInputParameterException;
import br.com.ccs.messagedispatcher.exceptions.MessageHandlerMultipleInputParametersException;
import br.com.ccs.messagedispatcher.exceptions.MessageHandlerNotFoundException;
import br.com.ccs.messagedispatcher.exceptions.MessageHandlerWithoutInputParameterException;
import br.com.ccs.messagedispatcher.messaging.MessageAction;
import br.com.ccs.messagedispatcher.messaging.annotation.Command;
import br.com.ccs.messagedispatcher.messaging.annotation.Event;
import br.com.ccs.messagedispatcher.messaging.annotation.MessageHandler;
import br.com.ccs.messagedispatcher.messaging.annotation.Notification;
import br.com.ccs.messagedispatcher.messaging.annotation.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.support.AopUtils;
import org.springframework.boot.test.util.ApplicationContextTestUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
@SuppressWarnings("unused")
@Component
public class MessageDispatcherAnnotatedMethodDiscoverImpl implements MessageDispatcherAnnotatedMethodDiscover {

    private static final Logger log = LoggerFactory.getLogger(MessageDispatcherAnnotatedMethodDiscoverImpl.class);

    private final Map<MessageAction, HashMap<String, Method>> handlers;

    private final ApplicationContext applicationContext;

    public MessageDispatcherAnnotatedMethodDiscoverImpl(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
        this.handlers = Map.of(
                MessageAction.COMMAND, new HashMap<>(),
                MessageAction.QUERY, new HashMap<>(),
                MessageAction.NOTIFICATION, new HashMap<>(),
                MessageAction.EVENT, new HashMap<>());

        resolveAnnotatedMethods(applicationContext);
    }

    private void resolveAnnotatedMethods(ApplicationContext applicationContext) {
        var start = System.currentTimeMillis();
        final var listeners = MessageListenerBeanDiscover.getMessageListeners(applicationContext);
        listeners.forEach(this::registerMessageHandler);
        if (log.isDebugEnabled()) {
            log.debug("MessageHandlerMethodDiscover levou {} ms para descobrir todos o métodos handler.", System.currentTimeMillis() - start);
            log.debug("Listeners descobertos: {}", listeners.size());
            handlers.forEach((key, value) -> log.debug("Handlers {} descobertos: {}", key, value.size()));
        }
    }

    private void registerMessageHandler(Object listener) {
        final var listenerClass = AopUtils.getTargetClass(listener);
        final var listenerMethods = listenerClass.getMethods();

        Arrays.stream(listenerMethods)
                .filter(MessageDispatcherAnnotatedMethodDiscoverImpl::isAnnotationPresent)
                .forEach(method -> {
                    try {
                        var parameterType = method.getParameterTypes()[0].getSimpleName();

                        if (method.isAnnotationPresent(Command.class)) {
                            registreHandler(MessageAction.COMMAND, method, parameterType);
                            return;
                        }

                        if (method.isAnnotationPresent(Query.class)) {
                            registreHandler(MessageAction.QUERY, method, parameterType);
                            return;
                        }

                        if (method.isAnnotationPresent(Event.class)) {
                            registreHandler(MessageAction.EVENT, method, parameterType);
                            return;
                        }

                        if (method.isAnnotationPresent(Notification.class)) {
                            registreHandler(MessageAction.NOTIFICATION, method, parameterType);
                            return;
                        }

                        if (method.isAnnotationPresent(MessageHandler.class)) {
                            var annotation = method.getAnnotation(MessageHandler.class);
                            registreHandler(annotation.action(), method, parameterType);
                        }
                    } catch (ArrayIndexOutOfBoundsException e) {
                        var ex = new MessageHandlerWithoutInputParameterException("Handler não possui parâmetros de entrada.");
                        handleMappingError(ex);
                    } catch (MessageHandlerDuplicatedInputParameterException e) {
                        handleMappingError(e);
                    }
                });
    }

    private void registreHandler(MessageAction actionType, Method method, String parameterType) {
        log.debug("Registrando handler {} para o tipo {}", method.getName(), parameterType);

        if (method.getParameterCount() > 1) {
            throw new MessageHandlerMultipleInputParametersException("Tipo handler: " + actionType + " método: " + method
                    + " handlers devem conter apenas um parâmetro de entrada.");
        }

        if (handlers.get(actionType).containsKey(parameterType)) {
            throw new MessageHandlerDuplicatedInputParameterException("Tipo handler: " + actionType + " parâmetro de entrada duplicado no método: " + method
                    + " não é permitido utilizar o mesmo parâmetro de entrada para handlers do mesmo tipo.");
        }
        handlers.get(actionType).put(parameterType, method);
    }

    private static boolean isAnnotationPresent(Method method) {
        return method.isAnnotationPresent(Command.class) ||
                method.isAnnotationPresent(Query.class) ||
                method.isAnnotationPresent(Event.class) ||
                method.isAnnotationPresent(Notification.class) ||
                method.isAnnotationPresent(MessageHandler.class);
    }

    @Override
    public Method getHandler(MessageAction actionType, String parameterType) {
        var method = handlers.get(actionType).get(parameterType);

        if (method == null) {
            throw new MessageHandlerNotFoundException("Nenhum handler encontrado capaz de processar o tipo: " + parameterType);
        }

        return method;
    }

    private void handleMappingError(Exception e) {
        log.error("Erro ao registrar handler : {} ", e.getMessage(), e);
        ApplicationContextTestUtils.closeAll(applicationContext);
        System.exit(999);
    }
}
