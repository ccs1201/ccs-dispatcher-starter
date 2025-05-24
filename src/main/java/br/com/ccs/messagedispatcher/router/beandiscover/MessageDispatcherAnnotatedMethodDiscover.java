package br.com.ccs.messagedispatcher.router.beandiscover;

import br.com.ccs.messagedispatcher.exceptions.MessageHandlerDuplicatedParameterException;
import br.com.ccs.messagedispatcher.exceptions.MessageHandlerWithoutInputParameterException;
import br.com.ccs.messagedispatcher.messaging.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.support.AopUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Component
public class MessageDispatcherAnnotatedMethodDiscover {

    private static final Logger log = LoggerFactory.getLogger(MessageDispatcherAnnotatedMethodDiscover.class);

    private final Map<Class<?>, Method> commands;
    private final Map<Class<?>, Method> queries;
    private final Map<Class<?>, Method> notifications;
    private final Map<Class<?>, Method> events;
    private final Map<Class<?>, Method> messageHandlers;

    public MessageDispatcherAnnotatedMethodDiscover(ApplicationContext applicationContext) {
        this.commands = new HashMap<>();
        this.queries = new HashMap<>();
        this.notifications = new HashMap<>();
        this.events = new HashMap<>();
        this.messageHandlers = new HashMap<>();

        resolveAnnotatedMethods(applicationContext);
    }

    private void resolveAnnotatedMethods(ApplicationContext applicationContext) {
        var start = System.currentTimeMillis();
        final var listeners = MessageListenerBeanDiscover.getMessageListeners(applicationContext);
        listeners.forEach(this::registerHandler);
        log.debug("Listeners descobertos: {}", listeners.size());
        log.debug("Commands handlers descobertos: {}", commands.size());
        log.debug("Queries handlers descobertos: {}", queries.size());
        log.debug("Notifications handlers descobertos: {}", notifications.size());
        log.debug("Events handlers descobertos: {}", events.size());
        log.debug("MessageHandlers descobertos: {}", messageHandlers.size());
        log.debug("MessageHandlerMethodDiscover levou {} ms para descobrir todos o métodos handler.", System.currentTimeMillis() - start);
    }

    private void registerHandler(Object listener) {
        final var listenerClass = AopUtils.getTargetClass(listener);
        final var listenerMethods = listenerClass.getMethods();

        Arrays.stream(listenerMethods)
                .forEach(method -> {

                    if (!isAnnotationPresent(method)) {
                        return;
                    }

                    try {
                        var parameterType = method.getParameterTypes()[0];

                        if (method.isAnnotationPresent(Command.class)) {
                            registreCommand(method, parameterType);
                            return;
                        }

                        if (method.isAnnotationPresent(Query.class)) {
                            registreQuery(method, parameterType);
                            return;
                        }

                        if (method.isAnnotationPresent(Event.class)) {
                            registreEvent(method, parameterType);
                            return;
                        }

                        if (method.isAnnotationPresent(Notification.class)) {
                            registreNotification(method, parameterType);
                            return;
                        }

                        if (method.isAnnotationPresent(MessageHandler.class)) {
                            if (messageHandlers.containsKey(method.getParameterTypes()[0])) {
                                throw new IllegalArgumentException("Handler duplicado para o tipo " + parameterType.getName());
                            }
                            messageHandlers.put(parameterType, method);
                        }
                    } catch (ArrayIndexOutOfBoundsException e) {
                        var ex = new MessageHandlerWithoutInputParameterException("Handler não possui parâmetros de entrada.");
                        handleMappingError(method, ex.getMessage(), ex);
                    } catch (MessageHandlerDuplicatedParameterException e) {
                        handleMappingError(method, e.getMessage(), e);
                    }
                });
    }

    private static boolean isAnnotationPresent(Method method) {
        return method.isAnnotationPresent(Command.class) ||
                method.isAnnotationPresent(Query.class) ||
                method.isAnnotationPresent(Event.class) ||
                method.isAnnotationPresent(Notification.class) ||
                method.isAnnotationPresent(MessageHandler.class);
    }

    private void registreMessageHandler(Method method, Class<?> parameterType) {
        if (messageHandlers.containsKey(parameterType)) {
            throw new MessageHandlerDuplicatedParameterException("Message handler duplicado para o tipo " + parameterType.getName());
        }
        messageHandlers.put(parameterType, method);
    }

    private void registreNotification(Method method, Class<?> parameterType) {
        if (notifications.containsKey(method.getParameterTypes()[0])) {
            throw new MessageHandlerDuplicatedParameterException("Notification handler duplicado para o tipo " + parameterType.getSimpleName());
        }
        notifications.put(parameterType, method);
    }

    private void registreEvent(Method method, Class<?> parameterType) {
        if (events.containsKey(method.getParameterTypes()[0])) {
            throw new MessageHandlerDuplicatedParameterException("Event handler duplicado para o tipo " + parameterType.getSimpleName());
        }
        events.put(parameterType, method);
    }

    private void registreQuery(Method method, Class<?> parameterType) {
        if (queries.containsKey(method.getParameterTypes()[0])) {
            throw new MessageHandlerDuplicatedParameterException("Query handler duplicado para o tipo " + parameterType.getSimpleName());
        }
        queries.put(parameterType, method);
    }

    private void registreCommand(Method method, Class<?> parameterType) {
        if (commands.containsKey(parameterType)) {
            throw new MessageHandlerDuplicatedParameterException("Command handler duplicado para o tipo " + parameterType.getSimpleName());
        }
        commands.put(parameterType, method);
    }

    private void handleMappingError(Method method, String message, Exception e) {
        log.error("Erro ao registrar message handler : {} {}", method, message, e);
        System.exit(999);
    }

}
