package br.com.ccs.messagedispatcher.pocs;

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

        resolve(applicationContext);
    }

    private void resolve(ApplicationContext applicationContext) {
        var start = System.currentTimeMillis();
        final var listeners = MessageListenerBeanDiscover.getMessageListeners(applicationContext);
        listeners.forEach(listener -> mapMethods(listener));
        log.debug("Listeners descobertos: {}", listeners.size());
        log.debug("Commands handlers descobertos: {}", commands.size());
        log.debug("Queries handlers descobertos: {}", queries.size());
        log.debug("Notifications handlers descobertos: {}", notifications.size());
        log.debug("Events handlers descobertos: {}", events.size());
        log.debug("MessageHandlers descobertos: {}", messageHandlers.size());
        log.debug("MessageHandlerMethodDiscover levou {} ms para descobrir todos o métodos handler.", System.currentTimeMillis() - start);
    }

    private void mapMethods(Object listener) {

        final var listenerClass = AopUtils.getTargetClass(listener);

        final var listenerMethods = listenerClass.getMethods();

        Arrays.stream(listenerMethods)
                .forEach(method -> {

                    if (!method.isAnnotationPresent(Command.class) &&
                            !method.isAnnotationPresent(Query.class) &&
                            !method.isAnnotationPresent(Event.class) &&
                            !method.isAnnotationPresent(Notification.class) &&
                            !method.isAnnotationPresent(MessageHandler.class)) {
                        return;
                    }

                    try {
                        var parameterType = method.getParameterTypes()[0];

                        if (method.isAnnotationPresent(Command.class)) {
                            if (commands.containsKey(parameterType)) {
                                throw new IllegalArgumentException("Command handler duplicado para o tipo " + parameterType.getName());
                            }
                            commands.put(parameterType, method);
                            return;
                        }

                        if (method.isAnnotationPresent(Query.class)) {
                            if (queries.containsKey(method.getParameterTypes()[0])) {
                                throw new IllegalArgumentException("Query handler duplicado para o tipo " + parameterType.getName());
                            }
                            queries.put(parameterType, method);
                            return;
                        }

                        if (method.isAnnotationPresent(Event.class)) {
                            if (events.containsKey(method.getParameterTypes()[0])) {
                                throw new IllegalArgumentException("Event handler duplicado para o tipo " + parameterType.getName());
                            }
                            events.put(parameterType, method);
                            return;
                        }

                        if (method.isAnnotationPresent(Notification.class)) {
                            if (notifications.containsKey(method.getParameterTypes()[0])) {
                                throw new IllegalArgumentException("Notification handler duplicado para o tipo " + parameterType.getName());
                            }
                            notifications.put(parameterType, method);
                            return;
                        }

                        if (method.isAnnotationPresent(MessageHandler.class)) {
                            if (messageHandlers.containsKey(method.getParameterTypes()[0])) {
                                throw new IllegalArgumentException("Message handler duplicado para o tipo " + parameterType.getName());
                            }
                            messageHandlers.put(parameterType, method);
                        }
                    } catch (ArrayIndexOutOfBoundsException e) {
                        log.error("Erro ao mapear metodo : " + method + " métodos com anotações de MessageHandler devem" +
                                "possuir parametros de entrada.", e);
                        System.exit(999);

                    } catch (IllegalArgumentException e) {
                        log.error("Erro ao mapear metodo : " + method + " " + e.getMessage(), e);
                        System.exit(999);
                    }
                });
    }
}
