package br.com.messagedispatcher.router.impl;

import br.com.messagedispatcher.annotation.Command;
import br.com.messagedispatcher.annotation.MessageListener;
import br.com.messagedispatcher.annotation.Notification;
import br.com.messagedispatcher.annotation.Query;
import br.com.messagedispatcher.beandiscover.MessageDispatcherAnnotatedHandlerDiscover;
import br.com.messagedispatcher.exceptions.MessageHandlerNotFoundException;
import br.com.messagedispatcher.exceptions.MessageRouterMissingHeaderException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.context.ApplicationContext;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import static br.com.messagedispatcher.model.HandlerType.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AnnotatedMessageRouterTest {

    @InjectMocks
    private AnnotatedMessageRouter router;

    @Mock
    private MessageDispatcherAnnotatedHandlerDiscover handlerDiscover;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private ApplicationContext applicationContext;

    @Mock
    private Message message;


//    @BeforeEach
//    void setUp() {
//        router = new AnnotatedMessageRouter(objectMapper, handlerDiscover, applicationContext);
//    }

    @Test
    void routeMessage_shouldThrowExceptionWhenMessageTypeHeaderIsMissing() {
        // Arrange
        Message message = createMessage(null, TestPayload.class.getName());

        // Act & Assert
        assertThrows(MessageRouterMissingHeaderException.class, () -> router.routeMessage(message));
    }

    @Test
    void routeMessage_shouldThrowExceptionWhenPayloadClassHeaderIsMissing() {
        // Arrange
        Message message = createMessage(COMMAND.name(), null);

        // Act & Assert
        assertThrows(MessageRouterMissingHeaderException.class, () -> router.routeMessage(message));
    }

    @Test
    void routeMessage_shouldThrowExceptionWhenHandlerNotFound() {
        // Arrange
        Message message = createMessage(COMMAND.name(), TestPayload.class.getName());
        when(handlerDiscover.getHandler(any(), any())).thenThrow(MessageHandlerNotFoundException.class);

        // Act & Assert
        assertThrows(MessageHandlerNotFoundException.class, () -> router.routeMessage(message));
    }

    @Test
    void routeMessage_shouldInvokeCommandHandler() throws Exception {
        // Arrange
        Message message = createMessage(COMMAND.name(), TestPayload.class.getName());
        TestHandler handler = new TestHandler();
        Method method = TestHandler.class.getMethod("handleCommand", TestPayload.class);

        when(handlerDiscover.getHandler(eq(COMMAND), eq(TestPayload.class.getName())))
                .thenReturn(method);

        // Act
        Object result = router.routeMessage(message);

        // Assert
        assertEquals("command handled", result);
    }

    @Test
    void routeMessage_shouldInvokeQueryHandler() throws Exception {
        // Arrange
        Message message = createMessage(QUERY.name(), TestPayload.class.getName());
        TestHandler handler = new TestHandler();
        Method method = TestHandler.class.getMethod("handleQuery", TestPayload.class);

        when(handlerDiscover.getHandler(eq(QUERY), eq(TestPayload.class.getName())))
                .thenReturn(method);

        // Act
        Object result = router.routeMessage(message);

        // Assert
        assertEquals("query handled", result);
    }

    @Test
    void routeMessage_shouldInvokeNotificationHandler() throws Exception {
        // Arrange
        Message message = createMessage(NOTIFICATION.name(), TestPayload.class.getName());
        TestHandler handler = new TestHandler();
        Method method = TestHandler.class.getMethod("handleNotification", TestPayload.class);

        when(handlerDiscover.getHandler(eq(NOTIFICATION), eq(TestPayload.class.getName())))
                .thenReturn(method);

        // Act
        Object result = router.routeMessage(message);

        // Assert
        assertEquals(null, result);
    }

    private Message createMessage(String messageType, String payloadClass) {
        MessageProperties props = new MessageProperties();
        Map<String, Object> headers = new HashMap<>();

        if (messageType != null) {
            headers.put(messageType, messageType);
        }

        if (payloadClass != null) {
            headers.put(messageType, payloadClass);
        }

        props.setHeaders(headers);

        when(message.getMessageProperties()).thenReturn(props);
        return message;
    }

    @MessageListener
    static class TestHandler {
        @Command
        public String handleCommand(TestPayload payload) {
            return "command handled";
        }

        @Query
        public String handleQuery(TestPayload payload) {
            return "query handled";
        }

        @Notification
        public void handleNotification(TestPayload payload) {
            // Do nothing
        }
    }

    static class TestPayload {
        private String data = "test";

        public String getData() {
            return data;
        }
    }
}