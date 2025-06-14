package br.com.messagedispatcher.config.rabbitmq;

import br.com.messagedispatcher.config.properties.MessageDispatcherProperties;
import br.com.messagedispatcher.exceptions.MessageDispatcherBeanResolutionException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.Declarables;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.core.Queue;

import static br.com.messagedispatcher.constants.MessageDispatcherConstants.Exchange.DIRECT;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ExchangesQueuesBindingConfigAutoConfigTest {

    @Mock
    private MessageDispatcherProperties properties;

    /**
     * Tests the behavior of deadLetterExchangeAndQueue method when properties contain null values.
     * This test verifies that the method throws a NullPointerException when essential
     * property values are null.
     */
    @Test
    public void testDeadLetterExchangeAndQueue_NullProperties() {
        ExchangesQueuesBindingConfigAutoConfig config = new ExchangesQueuesBindingConfigAutoConfig();

        when(properties.getDeadLetterExchangeName()).thenReturn(null);

        assertThrows(MessageDispatcherBeanResolutionException.class, () -> config.deadLetterExchangeAndQueue(properties));
    }

    /**
     * Tests that the defaultExchangeAndQueue method throws a MessageDispatcherBeanResolutionException
     * when an unsupported exchange type is provided in the MessageDispatcherProperties.
     */
    @Test
    public void testDefaultExchangeAndQueue_UnsupportedExchangeType() {
        ExchangesQueuesBindingConfigAutoConfig config = new ExchangesQueuesBindingConfigAutoConfig();

        properties.setExchangeType(null);

        assertThrows(MessageDispatcherBeanResolutionException.class, () -> config.defaultExchangeAndQueue(properties));
    }

    /**
     * Tests the deadLetterExchangeAndQueue method to ensure it creates and returns
     * a Declarables object with the correct exchange, queue, and binding configuration
     * based on the provided MessageDispatcherProperties.
     */
    @Test
    public void test_deadLetterExchangeAndQueue_createsCorrectDeclarables() {

        ExchangesQueuesBindingConfigAutoConfig config = new ExchangesQueuesBindingConfigAutoConfig();

        when(properties.getDeadLetterExchangeName()).thenReturn("test.dlx");
        when(properties.isDeadLetterExchangeDurable()).thenReturn(true);
        when(properties.getDeadLetterQueueName()).thenReturn("test.dlq");
        when(properties.getDeadLetterRoutingKey()).thenReturn("test.dlrk");
        when(properties.getDeadLetterExchangeType()).thenReturn(DIRECT);

        Declarables result = config.deadLetterExchangeAndQueue(properties);

        assertNotNull(result);
        assertEquals(3, result.getDeclarables().size());

        boolean hasExchange = false;
        boolean hasQueue = false;
        boolean hasBinding = false;

        for (Object declarable : result.getDeclarables()) {
            if (declarable instanceof Exchange exchange) {
                assertEquals("test.dlx", exchange.getName());
                assertEquals(ExchangeTypes.DIRECT, exchange.getType());
                hasExchange = true;
            } else if (declarable instanceof Queue queue) {
                assertEquals("test.dlq", queue.getName());
                hasQueue = true;
            } else if (declarable instanceof Binding binding) {
                assertEquals("test.dlx", binding.getExchange());
                assertEquals("test.dlq", binding.getDestination());
                assertEquals("test.dlrk", binding.getRoutingKey());
                hasBinding = true;
            }
        }

        assertTrue(hasExchange, "Declarables should contain an Exchange");
        assertTrue(hasQueue, "Declarables should contain a Queue");
        assertTrue(hasBinding, "Declarables should contain a Binding");
    }

    /**
     * Test case for defaultExchangeAndQueue method
     * Verifies that the method creates and returns a Declarables object
     * containing the correct Exchange, Queue, and Binding objects
     * based on the provided MessageDispatcherProperties.
     */
    @Test
    public void test_defaultExchangeAndQueue_createsCorrectDeclarables() {

        ExchangesQueuesBindingConfigAutoConfig config = new ExchangesQueuesBindingConfigAutoConfig();

        when(properties.getExchangeType()).thenReturn(DIRECT);
        when(properties.getExchangeName()).thenReturn("testExchange");
        when(properties.isExchangeDurable()).thenReturn(true);
        when(properties.getQueueName()).thenReturn("testQueue");
        when(properties.getDeadLetterExchangeName()).thenReturn("testDLX");
        when(properties.getDeadLetterRoutingKey()).thenReturn("testDLRK");
        when(properties.getRoutingKey()).thenReturn("testRoutingKey");

        Declarables result = config.defaultExchangeAndQueue(properties);

        assertNotNull(result);
        assertEquals(3, result.getDeclarables().size());
        assertTrue(result.getDeclarables().stream().anyMatch(d -> d instanceof Exchange));
        assertTrue(result.getDeclarables().stream().anyMatch(d -> d instanceof Queue));
        assertTrue(result.getDeclarables().stream().anyMatch(d -> d instanceof Binding));
    }

}
