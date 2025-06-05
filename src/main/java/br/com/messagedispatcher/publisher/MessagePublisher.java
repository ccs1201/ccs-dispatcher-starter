package br.com.messagedispatcher.publisher;

import org.springframework.lang.NonNull;

@SuppressWarnings("unused")
public interface MessagePublisher {
    /**
     * Publica um evento para a aplicação local através da exchange global.
     * Atua como um fire and forget, não esperando por uma resposta.
     * <p>
     * Publishes an event to local application through the global exchange.
     * Acts as a fire and forget, not waiting for a response.
     *
     * @param body - corpo da mensagem
     */
    void sendEvent(final Object body);

    /**
     * Publica um evento para uma aplicação através da exchange global.
     * Atua como um fire and forget, não esperando por uma resposta.
     * <p>
     * Publishes an event to am application through the global exchange.
     * Acts as a fire and forget, not waiting for a response.
     *
     * @param routingKey - chave de roteamento
     * @param body       - corpo da mensagem
     */
    void sendEvent(final String routingKey, final Object body);

    /**
     * Publica um evento para uma aplicação através da exchange informada.
     * Atua como um fire and forget, não esperando por uma resposta.
     * <p>
     * Publishes an event to an application through the defined exchange.
     * Acts as a fire and forget, not waiting for a response.
     *
     * @param exchange   - nome da exchange
     * @param routingKey - chave de roteamento
     * @param body       - corpo da mensagem
     */
    void sendEvent(final String exchange, final String routingKey, final Object body);

    /**
     * Publica uma mensagem do tipo command sem aguardar resposta.
     * Atua com um fire and forget.
     * <p>
     * Publishes a message of type command and no waits for a response.
     *
     * @param body - corpo da mensagem
     */
    void sendCommand(final Object body);

    /**
     * Publica uma mensagem do tipo command sem aguardar resposta.
     * Atua com um fire and forget.
     * <p>
     * Publishes a message of type command and no waits for a response.
     *
     * @param routingKey - chave de roteamento
     * @param body       - corpo da mensagem
     */
    void sendCommand(final String routingKey, final Object body);

    /**
     * Publica uma mensagem do tipo command sem aguardar resposta.
     * Atua com um fire and forget.
     * <p>
     * Publishes a message of type command and no waits for a response.
     *
     * @param exchange   - nome da exchange
     * @param routingKey - chave de roteamento
     * @param body       - corpo da mensagem
     */
    void sendCommand(final String exchange, final String routingKey, final Object body);

    /**
     * Publica uma mensagem do tipo command e aguarda um retorno.
     * <p>
     * Publishes a message of type command and waits for a response.
     *
     * @param body          - corpo da mensagem
     * @param responseClass - tipo para qual a reposta deve ser convertida
     * @param <T>           - tipo para qual a reposta deve ser convertida
     * @return - resposta convertida para o tipo informado
     */
    <T> T doCommand(final Object body, @NonNull final Class<T> responseClass);

    /**
     * Publica uma mensagem do tipo command e aguarda um retorno.
     * <p>
     * Publishes a message of type command and waits for a response.
     *
     * @param routingKey    - chave de roteamento
     * @param body          - corpo da mensagem
     * @param responseClass - tipo para qual a reposta deve ser convertida
     * @param <T>           - tipo para qual a reposta deve ser convertida
     * @return - resposta convertida para o tipo informado
     */
    <T> T doCommand(final String routingKey, final Object body, @NonNull final Class<T> responseClass);

    /**
     * Publica uma mensagem do tipo command e aguarda um retorno.
     * <p>
     * Publishes a message of type command and waits for a response.
     *
     * @param exchange      - nome da exchange
     * @param routingKey    - chave de roteamento
     * @param body          - corpo da mensagem
     * @param responseClass - tipo para qual a reposta deve ser convertida
     * @param <T>           - tipo para qual a reposta deve ser convertida
     * @return - resposta convertida para o tipo informado
     */
    <T> T doCommand(final String exchange, final String routingKey, final Object body, @NonNull final Class<T> responseClass);

    /**
     * Publica uma mensagem do tipo query e aguarda um retorno.
     * <p>
     * Publishes a message of type query and waits for a response.
     *
     * @param body          - corpo da mensagem
     * @param responseClass - tipo para qual a reposta deve ser convertida
     * @param <T>           - tipo para qual a reposta deve ser convertida
     * @return - resposta convertida para o tipo informado
     */
    <T> T doQuery(final Object body, @NonNull final Class<T> responseClass);

    /**
     * Publica uma mensagem do tipo query e aguarda um retorno.
     * <p>
     * Publishes a message of type query and waits for a response.
     *
     * @param routingKey    - chave de roteamento
     * @param body          - corpo da mensagem
     * @param responseClass - tipo para qual a reposta deve ser convertida
     * @param <T>           - tipo para qual a reposta deve ser convertida
     * @return - resposta convertida para o tipo informado
     */
    <T> T doQuery(final String routingKey, final Object body, @NonNull final Class<T> responseClass);

    /**
     * Publica uma mensagem do tipo query e aguarda um retorno.
     * <p>
     * Publishes a message of type query and waits for a response.
     *
     * @param exchange      - nome da exchange
     * @param routingKey    - chave de roteamento
     * @param body          - corpo da mensagem
     * @param responseClass - tipo para qual a reposta deve ser convertida
     * @param <T>           - tipo para qual a reposta deve ser convertida
     * @return - resposta convertida para o tipo informado
     */
    <T> T doQuery(final String exchange, final String routingKey, final Object body, @NonNull final Class<T> responseClass);

    /**
     * Publica uma notificação para a aplicação local através da exchange global.
     * <p>
     * Publishes a notification to local application through the global exchange.
     *
     * @param body - corpo da mensagem
     */
    void sendNotification(final Object body);

    /**
     * Publica uma notificação para uma aplicação através da exchange global.
     * <p>
     * Publishes a notification to an application through the global exchange.
     *
     * @param routingKey - chave de roteamento
     * @param body       - corpo da mensagem
     */
    void sendNotification(final String routingKey, final Object body);
}
