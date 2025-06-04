package br.com.messagedispatcher.publisher;

import org.springframework.lang.NonNull;

@SuppressWarnings("unused")
public interface MessagePublisher {
    void sendEvent(Object body);

    void sendEvent(String routingKey, Object body);

    void sendEvent(String exchange, String routingKey, Object body);

    <T> T doCommand(Object body, @NonNull Class<T> responseClass);

    <T> T doCommand(String routingKey, Object body, @NonNull Class<T> responseClass);

    <T> T doCommand(String exchange, String routingKey, Object body, @NonNull Class<T> responseClass);

    <T> T doQuery(Object body, @NonNull Class<T> responseClass);

    <T> T doQuery(String routingKey, Object body, @NonNull Class<T> responseClass);

    <T> T doQuery(String exchange, String routingKey, Object body, @NonNull Class<T> responseClass);

    void sendNotification(Object body);

    void sendNotification(String routingKey, Object body);
}
