package br.com.ccs.messagedispatcher.messaging.publisher.proxy;

import br.com.ccs.messagedispatcher.messaging.MessageType;

public interface TemplateProxy {

    /**
     * Publica uma mensagem para uma aplicação através da exchange informada e espera por uma resposta.
     * <p>
     * Publishes a body to an application through the defined exchange and waits for a response.
     *
     * @param exchange      - nome da exchange
     * @param routingKey    - chave de roteamento
     * @param body          - corpo da mensagem
     * @param responseClass - classe de retorno esperado
     * @param <T>           tipo de retorno esperado
     * @return (responseClass) object
     */
    <T> T convertSendAndReceive(String exchange, String routingKey, Object body, Class<T> responseClass,
                                MessageType messageType);


    /**
     * Publica uma mensagem para uma aplicação através da exchange informada e não espera por uma resposta.
     * <p>
     * Publishes a body to an application through the defined exchange and no waits for a response.
     *
     * @param exchange   - nome da exchange
     * @param routingKey - chave de roteamento
     * @param body       - corpo da mensagem
     */
    void convertAndSend(String exchange, String routingKey, Object body, MessageType messageType);
}
