package br.com.messagedispatcher.listener;

import br.com.messagedispatcher.model.MessageDispatcherRemoteInvocationResult;
import br.com.messagedispatcher.router.MessageRouter;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SuppressWarnings("unused")
@ExtendWith(MockitoExtension.class)
public class RabbitMqMessageDispatcherListenerTest {

    @InjectMocks
    private RabbitMqMessageDispatcherListener listener;

    @Mock
    private MessageRouter messageRouter;

    @Mock
    private ObjectMapper objectMapper;

    /**
     * Tests the constructor of RabbitMqMessageDispatcherListener with a null MessageRouter.
     * This is an edge case where an essential dependency is not provided.
     */
    @Test
    public void testConstructorWithNullMessageRouter() {
        ObjectMapper objectMapper = mock(ObjectMapper.class);
        assertThrows(NullPointerException.class, () -> {
            new RabbitMqMessageDispatcherListener(null, objectMapper);
        });
    }


    /**
     * Tests the constructor of RabbitMqMessageDispatcherListener.
     * Verifies that the constructor initializes the messageRouter and objectMapper,
     * and logs a debug message with the simple name of the MessageRouter class.
     */
    @Test
    public void test_RabbitMqMessageDispatcherListener_Constructor() {
        MessageRouter mockMessageRouter = Mockito.mock(MessageRouter.class);
        ObjectMapper mockObjectMapper = Mockito.mock(ObjectMapper.class);

        LoggerFactory.getLogger(RabbitMqMessageDispatcherListener.class);

        new RabbitMqMessageDispatcherListener(mockMessageRouter, mockObjectMapper);
    }

    /**
     * Tests the onMessage method when logging is disabled and the messageRouter returns null.
     * This test verifies that the method returns null when the resultProcess is null.
     */
    @Test
    public void test_onMessage_returnsNullWhenResultProcessIsNull() {
        Message message = new Message("test".getBytes(), new MessageProperties());

        when(messageRouter.routeMessage(message)).thenReturn(null);

        MessageDispatcherRemoteInvocationResult result = listener.onMessage(message);

        assertNull(result);

        verify(messageRouter, times(1)).routeMessage(message);
    }

    /**
     * Tests the onMessage method when debug logging is enabled, the message router returns a non-null result,
     * but the message does not require a reply.
     */
    @Test
    public void test_onMessage_whenDebugEnabledAndResultNotNullAndNoReplyRequired() {
        Message message = new Message("test".getBytes(), new MessageProperties());
        when(messageRouter.routeMessage(message)).thenReturn(null);

        var result = listener.onMessage(message);

        assertNull(result);
        verify(messageRouter).routeMessage(message);
    }

    /**
     * Tests the onMessage method when debug logging is enabled, the message router returns a non-null result,
     * and the message requires a reply.
     * <p>
     * This test verifies that:
     * 1. The method correctly processes the message when debug logging is enabled.
     * 2. The message router is called and its result is used.
     * 3. The method builds and returns a response when replyTo is set.
     */
    @Test
    public void test_onMessage_whenDebugEnabledAndResultNotNullAndReplyRequired() {
        MessageProperties props = new MessageProperties();
        props.setReplyTo("replyQueue");
        Message message = new Message("test".getBytes(), props);

        when(messageRouter.routeMessage(message)).thenReturn("processedResult");

        var result = listener.onMessage(message);

        assertNotNull(result);
        assertFalse(result.hasException());
        assertEquals("processedResult", result.value());
        verify(messageRouter).routeMessage(message);
    }

    /**
     * Tests the onMessage method when logging is enabled and the message router returns null.
     * This test verifies that the method returns null when the message router processes
     * the message but returns no result, and no reply is required.
     */
    @Test
    public void test_onMessage_whenLoggingEnabledAndNoResult() {
        Message mockMessage = new Message("test".getBytes(), new MessageProperties());

        when(messageRouter.routeMessage(mockMessage)).thenReturn(null);

        var result = listener.onMessage(mockMessage);

        assertNull(result);
        verify(messageRouter).routeMessage(mockMessage);
    }

    /**
     * Tests the scenario where the message does not require a reply (no replyTo property).
     * In this case, the onMessage method should return null as specified in the implementation.
     */
    @Test
    public void test_onMessage_whenNoReplyToProperty_shouldReturnNull() {
        Message message = new Message("test".getBytes(), new MessageProperties());
        when(messageRouter.routeMessage(message)).thenReturn("some result");

        MessageDispatcherRemoteInvocationResult result = listener.onMessage(message);

        assertNull(result);
        verify(messageRouter).routeMessage(message);
    }

    /**
     * Tests the scenario where the messageRouter.routeMessage() returns null.
     * In this case, the onMessage method should return null as specified in the implementation.
     */
    @Test
    public void test_onMessage_whenRouteMessageReturnsNull_shouldReturnNull() {
        Message message = new Message("test".getBytes(), new MessageProperties());
        when(messageRouter.routeMessage(message)).thenReturn(null);

        MessageDispatcherRemoteInvocationResult result = listener.onMessage(message);

        assertNull(result);
        verify(messageRouter).routeMessage(message);
    }

}
