package br.com.messagedispatcher.publisher;

import br.com.messagedispatcher.config.properties.MessageDispatcherProperties;
import br.com.messagedispatcher.publisher.proxy.TemplateProxy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static br.com.messagedispatcher.constants.MessageDispatcherConstants.HandlerType.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RabbitMessagePublisherTest {

    @Mock
    private TemplateProxy templateProxy;

    @Mock
    private MessageDispatcherProperties properties;

    private RabbitMessagePublisher publisher;

    @BeforeEach
    void setUp() {
        when(properties.getExchangeName()).thenReturn("test.exchange");
        when(properties.getRoutingKey()).thenReturn("test.routing.key");
        publisher = new RabbitMessagePublisher(templateProxy, properties);
    }

    @Test
    void sendEvent_shouldUseDefaultExchangeAndRoutingKey() {
        // Arrange
        Object payload = new TestPayload();

        // Act
        publisher.sendEvent(payload);

        // Assert
        verify(templateProxy).convertAndSend(eq("test.exchange"), eq("test.routing.key"), eq(payload), eq(EVENT));
    }

    @Test
    void sendEvent_withRoutingKey_shouldUseDefaultExchangeAndProvidedRoutingKey() {
        // Arrange
        Object payload = new TestPayload();
        String routingKey = "custom.routing.key";

        // Act
        publisher.sendEvent(routingKey, payload);

        // Assert
        verify(templateProxy).convertAndSend(eq("test.exchange"), eq(routingKey), eq(payload), eq(EVENT));
    }

    @Test
    void sendEvent_withExchangeAndRoutingKey_shouldUseProvidedExchangeAndRoutingKey() {
        // Arrange
        Object payload = new TestPayload();
        String exchange = "custom.exchange";
        String routingKey = "custom.routing.key";

        // Act
        publisher.sendEvent(exchange, routingKey, payload);

        // Assert
        verify(templateProxy).convertAndSend(eq(exchange), eq(routingKey), eq(payload), eq(EVENT));
    }

    @Test
    void doCommand_shouldSendCommandMessageAndReturnResult() {
        // Arrange
        TestPayload payload = new TestPayload();
        when(templateProxy.convertSendAndReceive(any(), any(), any(), any(), any())).thenReturn(payload);

        // Act
        publisher.doCommand(payload, TestPayload.class);

        // Assert
        verify(templateProxy).convertSendAndReceive(eq(properties.getExchangeName()), eq("test.routing.key"),
                eq(payload), eq(TestPayload.class), eq(COMMAND));
    }

    @Test
    void doQuery_shouldSendQueryMessageAndReturnResult() {
        // Arrange
        TestPayload payload = new TestPayload();
        String routingKey = "query.routing.key";
        when(templateProxy.convertSendAndReceive(any(), any(), any(), any(), any())).thenReturn(payload);

        // Act
        publisher.doQuery(routingKey, payload, TestPayload.class);

        // Assert
        verify(templateProxy).convertSendAndReceive(eq(properties.getExchangeName()), eq(routingKey),
                eq(payload), eq(TestPayload.class), eq(QUERY));
    }

    @Test
    void sendNotification_shouldSendNotificationMessage() {
        // Arrange
        TestPayload payload = new TestPayload();
        String routingKey = "notification.routing.key";

        // Act
        publisher.sendNotification(routingKey, payload);

        // Assert
        verify(templateProxy).convertAndSend(eq(properties.getExchangeName()), eq(routingKey),
                eq(payload), eq(NOTIFICATION));
    }

    @SuppressWarnings("unused")
    static class TestPayload {

        public String getData() {
            return "test";
        }
    }
}