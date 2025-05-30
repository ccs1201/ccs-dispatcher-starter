package br.com.ccs.messagedispatcher.beandiscover.impl;

import br.com.ccs.messagedispatcher.beandiscover.MessageDispatcherAnnotatedMethodDiscover;
import br.com.ccs.messagedispatcher.exceptions.MessageHandlerDuplicatedInputParameterException;
import br.com.ccs.messagedispatcher.exceptions.MessageHandlerMultipleInputParametersException;
import br.com.ccs.messagedispatcher.exceptions.MessageHandlerNotFoundException;
import br.com.ccs.messagedispatcher.messaging.MessageKinda;
import br.com.ccs.messagedispatcher.messaging.annotation.Command;
import br.com.ccs.messagedispatcher.messaging.annotation.Event;
import br.com.ccs.messagedispatcher.messaging.annotation.MessageHandler;
import br.com.ccs.messagedispatcher.messaging.annotation.Notification;
import br.com.ccs.messagedispatcher.messaging.annotation.Query;
import br.com.ccs.messagedispatcher.util.validator.HandlerValidatorUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.support.AopUtils;
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

    private final Map<MessageKinda, HashMap<String, Method>> handlers;

    public MessageDispatcherAnnotatedMethodDiscoverImpl(ApplicationContext applicationContext) {
        this.handlers = Map.of(
                MessageKinda.COMMAND, new HashMap<>(),
                MessageKinda.QUERY, new HashMap<>(),
                MessageKinda.NOTIFICATION, new HashMap<>(),
                MessageKinda.EVENT, new HashMap<>());

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

                    if (method.isAnnotationPresent(Command.class)) {
                        registreHandler(MessageKinda.COMMAND, method);
                        return;
                    }

                    if (method.isAnnotationPresent(Query.class)) {
                        registreHandler(MessageKinda.QUERY, method);
                        return;
                    }

                    if (method.isAnnotationPresent(Event.class)) {
                        registreHandler(MessageKinda.EVENT, method);
                        return;
                    }

                    if (method.isAnnotationPresent(Notification.class)) {
                        registreHandler(MessageKinda.NOTIFICATION, method);
                        return;
                    }

                    if (method.isAnnotationPresent(MessageHandler.class)) {
                        var annotation = method.getAnnotation(MessageHandler.class);
                        registreHandler(annotation.kinda(), method);
                    }
                });
    }

    private void registreHandler(MessageKinda messageKinda, Method method) throws MessageHandlerMultipleInputParametersException, MessageHandlerDuplicatedInputParameterException {
        log.debug("Registrando handler {}", method.getName());
        HandlerValidatorUtil.validate(messageKinda, method, handlers.get(messageKinda));
        handlers.get(messageKinda).put(method.getParameterTypes()[0].getSimpleName(), method);
    }

    private static boolean isAnnotationPresent(Method method) {
        return method.isAnnotationPresent(Command.class) ||
                method.isAnnotationPresent(Query.class) ||
                method.isAnnotationPresent(Event.class) ||
                method.isAnnotationPresent(Notification.class) ||
                method.isAnnotationPresent(MessageHandler.class);
    }

    @Override
    public Method getHandler(MessageKinda actionType, String parameterType) {
        var method = handlers.get(actionType).get(parameterType);

        if (method == null) {
            throw new MessageHandlerNotFoundException("Nenhum handler encontrado capaz de processar o tipo: " + parameterType);
        }

        return method;
    }
}
