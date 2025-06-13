package br.com.messagedispatcher.beandiscover.impl;

import br.com.messagedispatcher.annotation.Command;
import br.com.messagedispatcher.annotation.Event;
import br.com.messagedispatcher.annotation.MessageHandler;
import br.com.messagedispatcher.annotation.Notification;
import br.com.messagedispatcher.annotation.Query;
import br.com.messagedispatcher.beandiscover.MessageDispatcherAnnotatedHandlerDiscover;
import br.com.messagedispatcher.exceptions.MessageHandlerDuplicatedInputParameterException;
import br.com.messagedispatcher.exceptions.MessageHandlerMultipleInputParametersException;
import br.com.messagedispatcher.exceptions.MessageHandlerNotFoundException;
import br.com.messagedispatcher.util.validator.HandlerValidatorUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.support.AopUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static br.com.messagedispatcher.constants.MessageDispatcherConstants.HandlerType;
import static java.util.Objects.isNull;

@SuppressWarnings("unused")
@Component
public class MessageDispatcherAnnotatedHandlerDiscoverImpl implements MessageDispatcherAnnotatedHandlerDiscover {

    private static final Logger log = LoggerFactory.getLogger(MessageDispatcherAnnotatedHandlerDiscoverImpl.class);

    private final Map<HandlerType, HashMap<String, Method>> handlers;

    public MessageDispatcherAnnotatedHandlerDiscoverImpl(ApplicationContext applicationContext) {
        this.handlers = Map.of(
                HandlerType.COMMAND, new HashMap<>(),
                HandlerType.QUERY, new HashMap<>(),
                HandlerType.NOTIFICATION, new HashMap<>(),
                HandlerType.EVENT, new HashMap<>());

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
                .filter(MessageDispatcherAnnotatedHandlerDiscoverImpl::isAnnotationPresent)
                .forEach(method -> {

                    if (method.isAnnotationPresent(Command.class)) {
                        registreHandler(HandlerType.COMMAND, method);
                        return;
                    }

                    if (method.isAnnotationPresent(Query.class)) {
                        registreHandler(HandlerType.QUERY, method);
                        return;
                    }

                    if (method.isAnnotationPresent(Event.class)) {
                        registreHandler(HandlerType.EVENT, method);
                        return;
                    }

                    if (method.isAnnotationPresent(Notification.class)) {
                        registreHandler(HandlerType.NOTIFICATION, method);
                        return;
                    }

                    if (method.isAnnotationPresent(MessageHandler.class)) {
                        var annotation = method.getAnnotation(MessageHandler.class);
                        registreHandler(annotation.handlerType(), method);
                    }
                });
    }

    private void registreHandler(HandlerType handlerType, Method method) throws MessageHandlerMultipleInputParametersException, MessageHandlerDuplicatedInputParameterException {
        log.debug("Registrando handler {}", method.getName());
        HandlerValidatorUtil.validate(handlerType, method, handlers.get(handlerType));
        handlers.get(handlerType).put(method.getParameterTypes()[0].getSimpleName(), method);
    }

    private static boolean isAnnotationPresent(Method method) {
        return method.isAnnotationPresent(Command.class) ||
                method.isAnnotationPresent(Query.class) ||
                method.isAnnotationPresent(Event.class) ||
                method.isAnnotationPresent(Notification.class) ||
                method.isAnnotationPresent(MessageHandler.class);
    }

    @Override
    public Method getHandler(HandlerType handlerType, String parameterType) {
        var method = handlers.get(handlerType).get(parameterType);

        if (isNull(method)) {
            throw new MessageHandlerNotFoundException("Nenhum handler encontrado capaz de processar o tipo: " + parameterType);
        }

        return method;
    }
}
