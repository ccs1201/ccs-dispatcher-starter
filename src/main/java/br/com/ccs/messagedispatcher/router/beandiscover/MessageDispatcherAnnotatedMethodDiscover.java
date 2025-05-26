package br.com.ccs.messagedispatcher.router.beandiscover;

import br.com.ccs.messagedispatcher.exceptions.MessageHandlerDuplicatedInputParameterException;
import br.com.ccs.messagedispatcher.exceptions.MessageHandlerWithoutInputParameterException;
import br.com.ccs.messagedispatcher.messaging.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.support.AopUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Component
public class MessageDispatcherAnnotatedMethodDiscover {

    private static final Logger log = LoggerFactory.getLogger(MessageDispatcherAnnotatedMethodDiscover.class);

    private final Map<Class<? extends Annotation>, HashMap<Class<?>, Method>> handlers;
//    private final Map<Class<?>, Method> commands;
//    private final Map<Class<?>, Method> queries;
//    private final Map<Class<?>, Method> notifications;
//    private final Map<Class<?>, Method> events;
//    private final Map<Class<?>, Method> messageHandlers;

    public MessageDispatcherAnnotatedMethodDiscover(ApplicationContext applicationContext) {
        this.handlers = Map.of(
                Command.class, new HashMap<Class<?>, Method>(),
                Query.class, new HashMap<Class<?>, Method>(),
                Notification.class, new HashMap<Class<?>, Method>(),
                Event.class, new HashMap<Class<?>, Method>(),
                MessageHandler.class, new HashMap<Class<?>, Method>()
        );


//        this.commands = new HashMap<>();
//        this.queries = new HashMap<>();
//        this.notifications = new HashMap<>();
//        this.events = new HashMap<>();
//        this.messageHandlers = new HashMap<>();

        resolveAnnotatedMethods(applicationContext);
    }

    private void resolveAnnotatedMethods(ApplicationContext applicationContext) {
        var start = System.currentTimeMillis();
        final var listeners = MessageListenerBeanDiscover.getMessageListeners(applicationContext);
        listeners.forEach(this::registerMessageHandler);
//        log.debug("Commands handlers descobertos: {}", commands.size());
//        log.debug("Queries handlers descobertos: {}", queries.size());
//        log.debug("Notifications handlers descobertos: {}", notifications.size());
//        log.debug("Events handlers descobertos: {}", events.size());
//        log.debug("MessageHandlers descobertos: {}", messageHandlers.size());
//        log.debug("Handlers descobertos: {}", handlers.size());
        if (log.isDebugEnabled()) {
            log.debug("MessageHandlerMethodDiscover levou {} ms para descobrir todos o métodos handler.", System.currentTimeMillis() - start);
            log.debug("Listeners descobertos: {}", listeners.size());
            handlers.forEach((key, value) -> log.debug("Handlers {} descobertos: {}", key.getSimpleName(), value.size()));
        }
    }

    private void registerMessageHandler(Object listener) {
        final var listenerClass = AopUtils.getTargetClass(listener);
        final var listenerMethods = listenerClass.getMethods();

        Arrays.stream(listenerMethods)
                .filter(MessageDispatcherAnnotatedMethodDiscover::isAnnotationPresent)
                .forEach(method -> {
                    try {
                        var parameterType = method.getParameterTypes()[0];

                        if (method.isAnnotationPresent(Command.class)) {
                            registreHandler(Command.class, method, parameterType);
                            return;
                        }

                        if (method.isAnnotationPresent(Query.class)) {
                            registreHandler(Query.class, method, parameterType);
//                            registreQuery(method, parameterType);
                            return;
                        }

                        if (method.isAnnotationPresent(Event.class)) {
                            registreHandler(Event.class, method, parameterType);
//                            registreEvent(method, parameterType);
                            return;
                        }

                        if (method.isAnnotationPresent(Notification.class)) {
                            registreHandler(Notification.class, method, parameterType);
//                            registreNotification(method, parameterType);
                            return;
                        }

                        if (method.isAnnotationPresent(MessageHandler.class)) {
//                            if (messageHandlers.containsKey(method.getParameterTypes()[0])) {
//                                throw new IllegalArgumentException("Handler duplicado para o tipo " + parameterType.getName());
//                            }
                            registreHandler(MessageHandler.class, method, parameterType);
//                            messageHandlers.put(parameterType, method);
                        }
                    } catch (ArrayIndexOutOfBoundsException e) {
                        var ex = new MessageHandlerWithoutInputParameterException("Handler não possui parâmetros de entrada.");
                        handleMappingError(ex);
                    } catch (MessageHandlerDuplicatedInputParameterException e) {
                        handleMappingError(e);
                    }
                });
    }

    private void registreHandler(Class<? extends Annotation> handlerAnnotation, Method method, Class<?> parameterType) {
        log.debug("Registrando handler {} para o tipo {}", method.getName(), parameterType.getName());

        if (handlers.get(handlerAnnotation).containsKey(parameterType)) {
            throw new MessageHandlerDuplicatedInputParameterException("Tipo handler: " + handlerAnnotation.getSimpleName() + " parâmetro de entrada duplicado no método: " + method
                    + " não é permitido utilizar o mesmo parâmetro de entrada para handlers do mesmo tipo.");
        }
        handlers.get(handlerAnnotation).put(parameterType, method);
    }

    private static boolean isAnnotationPresent(Method method) {
        return method.isAnnotationPresent(Command.class) ||
                method.isAnnotationPresent(Query.class) ||
                method.isAnnotationPresent(Event.class) ||
                method.isAnnotationPresent(Notification.class) ||
                method.isAnnotationPresent(MessageHandler.class);
    }

//    private void registreMessageHandler(Method method, Class<?> parameterType) {
//        if (messageHandlers.containsKey(parameterType)) {
//            throw new MessageHandlerDuplicatedInputParameterException("Message handler duplicado para o tipo " + parameterType.getSimpleName());
//        }
//        messageHandlers.put(parameterType, method);
//    }
//
//    private void registreNotification(Method method, Class<?> parameterType) {
//        if (notifications.containsKey(method.getParameterTypes()[0])) {
//            throw new MessageHandlerDuplicatedInputParameterException("Notification handler duplicado para o tipo " + parameterType.getSimpleName());
//        }
//        notifications.put(parameterType, method);
//    }
//
//    private void registreEvent(Method method, Class<?> parameterType) {
//        if (events.containsKey(method.getParameterTypes()[0])) {
//            throw new MessageHandlerDuplicatedInputParameterException("Event handler duplicado para o tipo " + parameterType.getSimpleName());
//        }
//        events.put(parameterType, method);
//    }
//
//    private void registreQuery(Method method, Class<?> parameterType) {
//        if (queries.containsKey(method.getParameterTypes()[0])) {
//            throw new MessageHandlerDuplicatedInputParameterException("Query handler duplicado para o tipo " + parameterType.getSimpleName());
//        }
//        queries.put(parameterType, method);
//    }
//
//    private void registreCommand(Method method, Class<?> parameterType) {
//        if (commands.containsKey(parameterType)) {
//            throw new MessageHandlerDuplicatedInputParameterException("Command handler duplicado para o tipo " + parameterType.getSimpleName());
//        }
//        commands.put(parameterType, method);
//    }

    private void handleMappingError(Exception e) {
        log.error("Erro ao registrar handler : {} ", e.getMessage(), e);
        System.exit(999);
    }

}
