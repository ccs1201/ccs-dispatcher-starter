package br.com.messagedispatcher.util.factory;

import br.com.messagedispatcher.constants.MessageDispatcherConstants;
import br.com.messagedispatcher.exceptions.MessageDispatcherBeanResolutionException;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.ExchangeBuilder;

import java.util.Arrays;
import java.util.Map;

import static br.com.messagedispatcher.constants.MessageDispatcherConstants.Exchange.CONSISTENT_HASH;
import static br.com.messagedispatcher.constants.MessageDispatcherConstants.Exchange.DIRECT;
import static br.com.messagedispatcher.constants.MessageDispatcherConstants.Exchange.HEADERS;
import static br.com.messagedispatcher.constants.MessageDispatcherConstants.Exchange.TOPIC;
import static java.util.Objects.isNull;

public class ExchangeFactory {
    public static Exchange buildExchange(String ExchangeName,
                                         boolean durable,
                                         MessageDispatcherConstants.Exchange exchangeType,
                                         Map<String, Object> arguments) {

        if (exchangeType == TOPIC) {
            return buildTopic(ExchangeName, durable);
        }

        if (exchangeType == DIRECT) {
            return buildDirect(ExchangeName, durable);
        }

        if (exchangeType == MessageDispatcherConstants.Exchange.FANOUT) {
            return buildFanout(ExchangeName, durable);
        }

        if (exchangeType == HEADERS) {
            return buildHeaders(ExchangeName, durable);
        }

        if (exchangeType == CONSISTENT_HASH) {
            if (isNull(arguments) || arguments.isEmpty()) {
                throw new MessageDispatcherBeanResolutionException("Argumentos para o tipo de exchange " + CONSISTENT_HASH.getType() + " não informados.");
            }
            return buildConsistentHash(ExchangeName, durable, arguments);
        }

        throw new MessageDispatcherBeanResolutionException("Não possível configurar a exchange, " +
                "verifique suas configurações e informe um tipo de exchange válido." + Arrays.toString(MessageDispatcherConstants.Exchange.values()));

    }

    private static Exchange buildHeaders(String name, boolean durable) {
        return ExchangeBuilder
                .headersExchange(name)
                .durable(durable)
                .build();
    }

    private static Exchange buildTopic(String name, boolean durable) {
        return ExchangeBuilder
                .topicExchange(name)
                .durable(durable)
                .build();
    }

    private static Exchange buildConsistentHash(String name, boolean durable, Map<String, Object> arguments) {
        return ExchangeBuilder
                .consistentHashExchange(name)
                .durable(durable)
                .withArguments(arguments)
                .build();
    }

    private static Exchange buildFanout(String name, boolean durable) {
        return ExchangeBuilder
                .fanoutExchange(name)
                .durable(durable)
                .build();
    }

    private static Exchange buildDirect(String name, boolean durable) {
        return ExchangeBuilder
                .directExchange(name)
                .durable(durable)
                .build();
    }
}