package br.com.ccs.dispatcher.config.rabbitmq;

import br.com.ccs.dispatcher.config.properties.DispatcherConfigurationProperties;
import br.com.ccs.dispatcher.messaging.exceptions.MessagePublishException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MessagePublisherTest {

    @InjectMocks
    private MessagePublisher messagePublisher;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private DispatcherConfigurationProperties properties;

    @Mock
    private RabbitTemplate rabbitTemplate;

    /**
     * Tests the fetch method when RabbitTemplate throws an AmqpException.
     * This scenario verifies that the method properly handles and wraps
     * the AmqpException in a MessagePublishException.
     */
    @Test
    public void testFetchHandlesAmqpException() {

        ObjectMapper objectMapper = new ObjectMapper();
        DispatcherConfigurationProperties properties = mock(DispatcherConfigurationProperties.class);

        when(properties.getExchangeName()).thenReturn("testExchange");
        when(rabbitTemplate.convertSendAndReceive(anyString(), anyString(), any(), any(MessagePostProcessor.class))).thenThrow(new AmqpException("Test AMQP error"));

        MessagePublisher messagePublisher = new MessagePublisher(rabbitTemplate, objectMapper, properties);

        assertThrows(MessagePublishException.class, () ->
            messagePublisher.fetch("testRoutingKey", "testBody", String.class);
        }
    }

    /**
     * Tests the behavior of fetch method when RabbitTemplate throws an AmqpException.
     * This scenario is explicitly handled in the focal method by wrapping the AmqpException
     * in a MessagePublishException.
     */
    @Test
    public void testFetchHandlesAmqpException_2() {

        ObjectMapper objectMapper = mock(ObjectMapper.class);
        MessagePublisher messagePublisher = new MessagePublisher(rabbitTemplate, objectMapper, null);

        when(rabbitTemplate.convertSendAndReceive(anyString(), anyString(), any(), any(MessagePostProcessor.class)))
                .thenThrow(new AmqpException("Test AMQP exception"));

        assertThrows(MessagePublishException.class, () ->
                messagePublisher.fetch("exchange", "routingKey", "body", String.class));
    }

    /**
     * Tests the fetch method when RabbitTemplate returns null.
     * This scenario verifies that the method throws a MessagePublishException
     * when no response is received from the message broker.
     */
    @Test
    public void testFetchHandlesNullResponse() {

        ObjectMapper objectMapper = new ObjectMapper();
        DispatcherConfigurationProperties properties = mock(DispatcherConfigurationProperties.class);

        when(properties.getExchangeName()).thenReturn("testExchange");
        when(rabbitTemplate.convertSendAndReceive(anyString(), anyString(), any(), any(MessagePostProcessor.class))).thenReturn(null);

        MessagePublisher messagePublisher = new MessagePublisher(rabbitTemplate, objectMapper, properties);

        assertThrows(MessagePublishException.class, () -> {
            messagePublisher.fetch("testRoutingKey", "testBody", String.class);
        });
    }

    /**
     * Tests the behavior of fetch method when RabbitTemplate returns null.
     * This scenario is explicitly handled in the focal method by throwing
     * a MessagePublishException when the response is null.
     */
    @Test
    public void testFetchHandlesNullResponse_2() {

        ObjectMapper objectMapper = mock(ObjectMapper.class);
        MessagePublisher messagePublisher = new MessagePublisher(rabbitTemplate, objectMapper, null);

        when(rabbitTemplate.convertSendAndReceive(anyString(), anyString(), any(), any(MessagePostProcessor.class)))
                .thenReturn(null);

        assertThrows(MessagePublishException.class, () ->
                messagePublisher.fetch("exchange", "routingKey", "body", String.class));
    }

    /**
     * Tests that the fetch method throws a MessagePublishException when no response is received
     * from the RabbitTemplate.
     */
    @Test
    public void testFetchThrowsMessagePublishExceptionWhenNoResponseReceived() {

        ObjectMapper objectMapper = mock(ObjectMapper.class);
        DispatcherConfigurationProperties properties = mock(DispatcherConfigurationProperties.class);

        when(properties.getExchangeName()).thenReturn("exchangeName");
        when(properties.getRoutingKey()).thenReturn("routingKey");
        when(rabbitTemplate.convertSendAndReceive(anyString(), anyString(), any(), any(MessagePostProcessor.class))).thenReturn(null);

        MessagePublisher messagePublisher = new MessagePublisher(rabbitTemplate, objectMapper, properties);

        assertThrows(MessagePublishException.class, () -> messagePublisher.fetch("body", String.class));
    }

    /**
     * Tests that the fetch method throws a MessagePublishException when the RabbitTemplate
     * encounters an AmqpException during message publishing.
     */
    @Test
    public void testFetchThrowsMessagePublishExceptionWhenRabbitTemplateThrowsAmqpException() {

        ObjectMapper objectMapper = mock(ObjectMapper.class);
        DispatcherConfigurationProperties properties = mock(DispatcherConfigurationProperties.class);

        when(properties.getExchangeName()).thenReturn("exchangeName");
        when(properties.getRoutingKey()).thenReturn("routingKey");
        when(rabbitTemplate.convertSendAndReceive(anyString(), anyString(), any(), any(MessagePostProcessor.class))).thenThrow(new AmqpException("Test exception"));

        MessagePublisher messagePublisher = new MessagePublisher(rabbitTemplate, objectMapper, properties);

        assertThrows(MessagePublishException.class, () -> messagePublisher.fetch("body", String.class));
    }

    /**
     * Test case for the fetch method with default exchange and routing key.
     * Verifies that the method correctly delegates to the overloaded fetch method
     * using the properties from DispatcherConfigurationProperties.
     */
    @Test
    public void testFetchWithDefaultExchangeAndRoutingKey() {
        // Implementation of test_fetch_1
        String testBody = "Test Body";
        String testExchange = "testExchange";
        String testRoutingKey = "testRoutingKey";
        Class<String> responseClass = String.class;

        MessagePublisher messagePublisher = new MessagePublisher(rabbitTemplate, objectMapper, properties);

        // Mock the behavior of properties
        when(properties.getExchangeName()).thenReturn(testExchange);
        when(properties.getRoutingKey()).thenReturn(testRoutingKey);

        // Mock the behavior of the overloaded fetch method
        when(rabbitTemplate.convertSendAndReceive(eq(testExchange), eq(testRoutingKey), eq(testBody), any(MessagePostProcessor.class)))
                .thenReturn("Test Response");

        String result = messagePublisher.fetch(testBody, responseClass);

        // Verify that the overloaded fetch method was called with correct parameters
        verify(rabbitTemplate).convertSendAndReceive(eq(testExchange), eq(testRoutingKey), eq(testBody), any(MessagePostProcessor.class));

        // Assert the result
        assertEquals("Test Response", result);
    }

    /**
     * Tests the MessagePublisher constructor with null ObjectMapper.
     * Expects a NullPointerException to be thrown.
     */
    @Test
    public void testMessagePublisherConstructorWithNullObjectMapper() {
        RabbitTemplate rabbitTemplate = new RabbitTemplate();
        DispatcherConfigurationProperties properties = new DispatcherConfigurationProperties();

        assertThrows(NullPointerException.class, () -> {
            new MessagePublisher(rabbitTemplate, null, properties);
        });
    }

    /**
     * Tests the MessagePublisher constructor with null DispatcherConfigurationProperties.
     * Expects a NullPointerException to be thrown.
     */
    @Test
    public void testMessagePublisherConstructorWithNullProperties() {
        RabbitTemplate rabbitTemplate = new RabbitTemplate();
        ObjectMapper objectMapper = new ObjectMapper();

        assertThrows(NullPointerException.class, () -> {
            new MessagePublisher(rabbitTemplate, objectMapper, null);
        });
    }

    /**
     * Tests the MessagePublisher constructor with null RabbitTemplate.
     * Expects a NullPointerException to be thrown.
     */
    @Test
    public void testMessagePublisherConstructorWithNullRabbitTemplate() {
        ObjectMapper objectMapper = new ObjectMapper();
        DispatcherConfigurationProperties properties = new DispatcherConfigurationProperties();

        assertThrows(NullPointerException.class, () -> {
            new MessagePublisher(null, objectMapper, properties);
        });
    }

    /**
     * Tests the sendEvent method when an AmqpException occurs during message publishing.
     * This test verifies that the method properly wraps the AmqpException
     * in a MessagePublishException when a failure occurs in message conversion or sending.
     */
    @Test
    public void testSendEventAmqpExceptionHandling() {

        DispatcherConfigurationProperties properties = mock(DispatcherConfigurationProperties.class);
        MessagePublisher messagePublisher = new MessagePublisher(rabbitTemplate, null, properties);

        doThrow(new AmqpException("Test exception")).when(rabbitTemplate).convertAndSend(anyString(), anyString(), any(), any(MessagePostProcessor.class));

        assertThrows(MessagePublishException.class, () -> messagePublisher.sendEvent("testRoutingKey", "testBody"));
    }

    /**
     * Test case for MessagePublisher constructor.
     * Verifies that a MessagePublisher instance can be created with valid parameters.
     */
    @Test
    public void testMessagePublisherConstructor() {

        ObjectMapper objectMapper = mock(ObjectMapper.class);
        DispatcherConfigurationProperties properties = mock(DispatcherConfigurationProperties.class);

        MessagePublisher messagePublisher = new MessagePublisher(rabbitTemplate, objectMapper, properties);

        assertNotNull(messagePublisher, "MessagePublisher instance should not be null");
    }

    /**
     * Test case for fetch method when no response is received.
     * This test verifies that a MessagePublishException is thrown when the RabbitTemplate
     * returns null as a response.
     */
    @Test
    public void testFetchNoResponseReceived() {
        when(rabbitTemplate.convertSendAndReceive(eq("exchange"), eq("routingKey"), any(), any(MessagePostProcessor.class))).thenReturn(null);

        assertThrows(MessagePublishException.class, () -> {
            messagePublisher.fetch("exchange", "routingKey", "testBody", String.class);
        });
    }

    /**
     * Test case for fetch method when a valid response is received
     * Verifies that the method correctly converts and returns the response
     */
    @Test
    public void test_fetch_whenValidResponseReceived_shouldReturnConvertedResponse() {
        // Arrange

        ObjectMapper objectMapper = mock(ObjectMapper.class);
        DispatcherConfigurationProperties properties = mock(DispatcherConfigurationProperties.class);
        MessagePublisher messagePublisher = new MessagePublisher(rabbitTemplate, objectMapper, properties);

        String exchange = "testExchange";
        String routingKey = "testRoutingKey";
        String body = "testBody";
        String response = "testResponse";
        String convertedResponse = "convertedTestResponse";

        when(rabbitTemplate.convertSendAndReceive(anyString(), anyString(), any(), any(MessagePostProcessor.class))).thenReturn(response);
        when(objectMapper.convertValue(any(), any(Class.class))).thenReturn(convertedResponse);

        // Act
        String result = messagePublisher.fetch(exchange, routingKey, body, String.class);

        // Assert
        assertEquals(convertedResponse, result);
        verify(rabbitTemplate).convertSendAndReceive(exchange, routingKey, body, any(MessagePostProcessor.class));
        verify(objectMapper).convertValue(response, String.class);
    }

    /**
     * Test case for fetch method with routing key, body, and response class
     * Verifies that the method correctly delegates to the overloaded fetch method
     * with the exchange name from properties
     */
    @Test
    public void test_fetch_with_routing_key_body_and_response_class() {
        // Arrange
        MessagePublisher messagePublisher = new MessagePublisher(rabbitTemplate, objectMapper, properties);
        String routingKey = "test.routing.key";
        String body = "test body";
        Class<String> responseClass = String.class;
        String exchangeName = "test.exchange";

        when(properties.getExchangeName()).thenReturn(exchangeName);
        when(rabbitTemplate.convertSendAndReceive(
                eq(exchangeName),
                eq(routingKey),
                eq(body),
                any(MessagePostProcessor.class)
        )).thenReturn("response");

        // Act
        String result = messagePublisher.fetch(routingKey, body, responseClass);

        // Assert
        verify(properties).getExchangeName();
        verify(rabbitTemplate).convertSendAndReceive(
                eq(exchangeName),
                eq(routingKey),
                eq(body),
                any(MessagePostProcessor.class)
        );
        verify(objectMapper).convertValue("response", responseClass);
    }

    /**
     * Test case for sendEvent(final Object body) method
     * Verifies that the method calls sendEvent with correct parameters from properties
     */
    @Test
    public void test_sendEvent_callsWithCorrectParameters() {
        // Arrange
        MessagePublisher messagePublisher = new MessagePublisher(rabbitTemplate, null, properties);
        Object testBody = new Object();
        String testExchange = "testExchange";
        String testRoutingKey = "testRoutingKey";

        when(properties.getExchangeName()).thenReturn(testExchange);
        when(properties.getRoutingKey()).thenReturn(testRoutingKey);

        // Act
        messagePublisher.sendEvent(testBody);

        // Assert
        verify(rabbitTemplate, times(1)).convertAndSend(eq(testExchange), eq(testRoutingKey), eq(testBody), any(MessagePostProcessor.class));
    }

    /**
     * Test case for sendEvent method when an AmqpException occurs.
     * This test verifies that a MessagePublishException is thrown when the RabbitTemplate
     * encounters an AmqpException while trying to send an event.
     */
    @Test
    public void test_sendEvent_throwsMessagePublishException_whenAmqpExceptionOccurs() {
        // Arrange
        RabbitTemplate rabbitTemplateMock = mock(RabbitTemplate.class);
        MessagePublisher messagePublisher = new MessagePublisher(rabbitTemplateMock, null, null);

        doThrow(new AmqpException("Test exception")).when(rabbitTemplateMock)
                .convertAndSend(eq("testExchange"), eq("testRoutingKey"), any(), any(MessagePostProcessor.class));

        // Act & Assert
        assertThrows(MessagePublishException.class, () ->
                messagePublisher.sendEvent("testExchange", "testRoutingKey", "testBody")
        );
    }

    /**
     * Tests that the sendEvent method throws a MessagePublishException when an AmqpException occurs.
     * This test verifies the error handling behavior of the sendEvent method when there's an issue
     * with publishing the event through RabbitMQ.
     */

    @Test
    public void test_sendEvent_throws_MessagePublishException_when_AmqpException_occurs() {
        // Setup

        MessagePublisher messagePublisher = new MessagePublisher(rabbitTemplate, null, null);

        String exchange = "testExchange";
        String routingKey = "testRoutingKey";
        Object body = new Object();

        doThrow(new AmqpException("Test AMQP exception")).when(rabbitTemplate).convertAndSend(
                eq(exchange), eq(routingKey), eq(body), any(MessagePostProcessor.class));

        // Test and verify
        assertThrows(MessagePublishException.class, () ->
                messagePublisher.sendEvent(exchange, routingKey, body)
        );
    }

    /**
     * Tests the sendEvent method with a routing key and body.
     * Verifies that the method calls sendEvent with the correct exchange name, routing key, and body.
     */
    @Test
    public void test_sendEvent_withRoutingKeyAndBody() {
        String routingKey = "test.routing.key";
        String body = "Test message body";
        String exchangeName = "test.exchange";

        when(properties.getExchangeName()).thenReturn(exchangeName);

        messagePublisher.sendEvent(routingKey, body);

        verify(rabbitTemplate).convertAndSend(eq(exchangeName), eq(routingKey), eq(body), any(MessagePostProcessor.class));
    }

}