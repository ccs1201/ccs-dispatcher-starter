package br.com.messagedispatcher.publisher.proxy;

import br.com.messagedispatcher.config.properties.MessageDispatcherProperties;
import br.com.messagedispatcher.exceptions.MessagePublisherTimeOutException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import static br.com.messagedispatcher.constants.MessageDispatcherConstants.HandlerType.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SuppressWarnings("unused")
@ExtendWith(MockitoExtension.class)
class RabbitTemplateProxyTest {

    @Mock
    private RabbitTemplate rabbitTemplate;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private MessageDispatcherProperties properties;

    @Mock
    private RabbitTemplateProxy proxy;

    @Test
    void sendMessage_shouldSendMessageWithCorrectHeaders() {
        // Arrange
        String exchange = "test.exchange";
        String routingKey = "test.routing.key";
        TestPayload payload = new TestPayload();

        // Act
        proxy.convertAndSend(exchange, routingKey, payload, EVENT);

        // Assert
        verify(proxy).convertAndSend(eq(exchange), eq(routingKey), eq(payload), any());
    }

    @Test
    void sendAndReceive_shouldHandleTimeoutException() {
        // Arrange
        String exchange = "test.exchange";
        String routingKey = "test.routing.key";
        TestPayload payload = new TestPayload();

        when(proxy.convertSendAndReceive(anyString(), anyString(), any(), any(), any()))
                .thenThrow(new MessagePublisherTimeOutException("Timeout", new RuntimeException("Timeout")));

        // Act & Assert
        assertThrows(MessagePublisherTimeOutException.class, () ->
                proxy.convertSendAndReceive(exchange, routingKey, payload, TestPayload.class, QUERY));
    }

    @Test
    void sendAndReceive_shouldReturnResponseWhenSuccessful() {
        // Arrange
        String exchange = "test.exchange";
        String routingKey = "test.routing.key";
        TestPayload payload = new TestPayload();
        TestPayload response = new TestPayload();

        when(proxy.convertSendAndReceive(anyString(), anyString(), any(), any(), any()))
                .thenReturn(response);

        // Act
        Object result = proxy.convertSendAndReceive(exchange, routingKey, payload, TestPayload.class, COMMAND);

        // Assert
        assertEquals(response, result);
        verify(proxy).convertSendAndReceive(eq(exchange), eq(routingKey), eq(payload), any(), any());
    }

    static class TestPayload {

        public String getData() {
            return "test";
        }
    }
}