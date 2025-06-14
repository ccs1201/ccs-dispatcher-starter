package br.com.messagedispatcher.util;

import br.com.messagedispatcher.constants.MessageDispatcherConstants;
import br.com.messagedispatcher.exceptions.MessageDispatcherBeanResolutionException;
import br.com.messagedispatcher.util.factory.ExchangeFactory;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.HeadersExchange;
import org.springframework.amqp.core.TopicExchange;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ExchangeFactoryTest {


    /**
     * Tests the buildExchange method with CONSISTENT_HASH type and empty arguments.
     * Expects a MessageDispatcherBeanResolutionException to be thrown.
     */
    @Test
    public void testBuildExchangeWithConsistentHashAndEmptyArguments() {
        Map<String, Object> emptyArguments = new HashMap<>();
        assertThrows(MessageDispatcherBeanResolutionException.class, () ->
                ExchangeFactory.buildExchange("TestExchange", true, MessageDispatcherConstants.Exchange.CONSISTENT_HASH, emptyArguments));
    }

    /**
     * Tests the buildExchange method with CONSISTENT_HASH type and null arguments.
     * Expects a MessageDispatcherBeanResolutionException to be thrown.
     */
    @Test
    public void testBuildExchangeWithConsistentHashAndNullArguments() {
        assertThrows(MessageDispatcherBeanResolutionException.class, () ->
                ExchangeFactory.buildExchange("TestExchange", true, MessageDispatcherConstants.Exchange.CONSISTENT_HASH, null));
    }

    /**
     * Tests the buildExchange method with an unsupported exchange type.
     * Expects a MessageDispatcherBeanResolutionException to be thrown.
     */
    @Test
    public void testBuildExchangeWithUnsupportedType() {
        assertThrows(MessageDispatcherBeanResolutionException.class, () ->
            ExchangeFactory.buildExchange("TestExchange", true, null, null));
    }

    /**
     * Tests the buildExchange method when the exchange type is TOPIC.
     * Verifies that a TopicExchange is created with the correct name and durability.
     */
    @Test
    public void test_buildExchangeWithTopicType() {
        String exchangeName = "testTopicExchange";
        boolean durable = true;
        MessageDispatcherConstants.Exchange exchangeType = MessageDispatcherConstants.Exchange.TOPIC;

        Exchange result = ExchangeFactory.buildExchange(exchangeName, durable, exchangeType, null);

        assertNotNull(result);
        assertInstanceOf(TopicExchange.class, result);
        assertEquals(exchangeName, result.getName());
        assertTrue(result.isDurable());
    }

    /**
     * Tests the buildExchange method with CONSISTENT_HASH exchange type and empty arguments.
     * Expects a MessageDispatcherBeanResolutionException to be thrown.
     */
    @Test
    public void test_buildExchange_consistentHashWithEmptyArguments() {
        assertThrows(MessageDispatcherBeanResolutionException.class, () ->
                ExchangeFactory.buildExchange("TestExchange", true, MessageDispatcherConstants.Exchange.CONSISTENT_HASH, new HashMap<>()));
    }

    /**
     * Tests the buildExchange method with CONSISTENT_HASH exchange type and valid arguments.
     * Verifies that a consistent hash exchange is created with the provided parameters.
     */
    @Test
    public void test_buildExchange_consistentHashWithValidArguments() {
        String exchangeName = "testExchange";
        boolean durable = true;
        MessageDispatcherConstants.Exchange exchangeType = MessageDispatcherConstants.Exchange.CONSISTENT_HASH;
        Map<String, Object> arguments = new HashMap<>();
        arguments.put("hash-header", "correlation-id");

        Exchange result = ExchangeFactory.buildExchange(exchangeName, durable, exchangeType, arguments);

        assertNotNull(result);
        assertEquals(exchangeName, result.getName());
        assertTrue(result.isDurable());
        assertEquals("x-consistent-hash", result.getType());
        assertEquals(arguments, result.getArguments());
    }

    /**
     * Tests the buildExchange method when the exchange type is HEADERS.
     * Verifies that a HeadersExchange is created with the correct name and durability.
     */
    @Test
    public void test_buildExchange_headers() {
        String exchangeName = "test-headers-exchange";
        boolean durable = true;
        MessageDispatcherConstants.Exchange exchangeType = MessageDispatcherConstants.Exchange.HEADERS;

        Exchange result = ExchangeFactory.buildExchange(exchangeName, durable, exchangeType, null);

        assertNotNull(result);
        assertInstanceOf(HeadersExchange.class, result);
        assertEquals(exchangeName, result.getName());
        assertTrue(result.isDurable());
    }

    /**
     * Tests the buildExchange method with an invalid exchange type.
     * Expects a MessageDispatcherBeanResolutionException to be thrown with the appropriate error message.
     */
    @Test
    public void test_buildExchange_invalidExchangeType() {
        String exchangeName = "testExchange";
        boolean durable = true;
        MessageDispatcherConstants.Exchange invalidExchangeType = null;

        MessageDispatcherBeanResolutionException exception = assertThrows(
                MessageDispatcherBeanResolutionException.class,
                () -> ExchangeFactory.buildExchange(exchangeName, durable, invalidExchangeType, null)
        );

        String expectedErrorMessage = "Não possível configurar a exchange, verifique suas configurações e informe um tipo de exchange válido." + Arrays.toString(MessageDispatcherConstants.Exchange.values());
        assertEquals(expectedErrorMessage, exception.getMessage());
    }

    /**
     * Tests that buildExchange returns a FanoutExchange when the exchangeType is FANOUT.
     * Path constraints: !((exchangeType == TOPIC)), !((exchangeType == DIRECT)), (exchangeType == Types.Exchange.FANOUT)
     */
    @Test
    public void test_buildExchange_returnsFanoutExchange() {
        String exchangeName = "testFanoutExchange";
        boolean durable = true;
        MessageDispatcherConstants.Exchange exchangeType = MessageDispatcherConstants.Exchange.FANOUT;

        Exchange result = ExchangeFactory.buildExchange(exchangeName, durable, exchangeType, null);

        assertNotNull(result);
        assertInstanceOf(FanoutExchange.class, result);
        assertEquals(exchangeName, result.getName());
        assertTrue(result.isDurable());
    }

    /**
     * Tests the buildExchange method when the exchange type is DIRECT.
     * Verifies that a DirectExchange is created with the correct name and durability.
     */
    @Test
    public void test_buildExchange_whenExchangeTypeIsDirect() {
        String exchangeName = "testDirectExchange";
        boolean durable = true;
        MessageDispatcherConstants.Exchange exchangeType = MessageDispatcherConstants.Exchange.DIRECT;

        Exchange result = ExchangeFactory.buildExchange(exchangeName, durable, exchangeType, null);

        assertNotNull(result);
        assertInstanceOf(DirectExchange.class, result);
        assertEquals(exchangeName, result.getName());
        assertTrue(result.isDurable());
    }
}
