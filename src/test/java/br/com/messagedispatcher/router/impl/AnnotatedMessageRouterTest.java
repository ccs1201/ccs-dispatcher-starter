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

import static br.com.messagedispatcher.constants.MessageDispatcherConstants.HandlerType.*;
import static br.com.messagedispatcher.constants.MessageDispatcherConstants.Headers.BODY_TYPE;
import static br.com.messagedispatcher.constants.MessageDispatcherConstants.Headers.HANDLER_TYPE;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

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

    @Test
    void routeMessage_shouldThrowExceptionWhenMessageTypeHeaderIsMissing() {

        Message message = createMessage(null, TestPayload.class.getName());

        assertThrows(MessageRouterMissingHeaderException.class, () -> router.routeMessage(message));

        verifyNoInteractions(handlerDiscover);
        verifyNoInteractions(objectMapper);
        verifyNoInteractions(applicationContext);
    }

    @Test
    void routeMessage_shouldThrowExceptionWhenPayloadClassHeaderIsMissing() {
        Message message = createMessage(COMMAND.name(), null);

        assertThrows(MessageRouterMissingHeaderException.class, () -> router.routeMessage(message));

        verifyNoInteractions(handlerDiscover);
        verifyNoInteractions(objectMapper);
        verifyNoInteractions(applicationContext);
    }

    @Test
    void routeMessage_shouldThrowExceptionWhenHandlerNotFound() {
        Message message = createMessage(COMMAND.name(), TestPayload.class.getName());
        when(handlerDiscover.getHandler(any(), any())).thenThrow(MessageHandlerNotFoundException.class);

        assertThrows(RuntimeException.class, () -> router.routeMessage(message));

        verify(handlerDiscover, times(1)).getHandler(eq(COMMAND), eq(TestPayload.class.getName()));
        verifyNoInteractions(objectMapper);
        verifyNoInteractions(applicationContext);
    }

    @Test
    void routeMessage_shouldInvokeCommandHandler() throws Exception {
        Message message = createMessage(COMMAND.name(), TestPayload.class.getName());
        TestHandler handler = new TestHandler();
        Method method = TestHandler.class.getMethod("handleCommand", TestPayload.class);

        when(handlerDiscover.getHandler(eq(COMMAND), eq(TestPayload.class.getName())))
                .thenReturn(method);

        when(objectMapper.readValue(eq(message.getBody()), eq(TestPayload.class)))
                .thenReturn(new TestPayload());

        when(applicationContext.getBean(eq(TestHandler.class)))
                .thenReturn(handler);

        Object result = router.routeMessage(message);

        assertEquals("command handled", result);

        verify(handlerDiscover, times(1)).getHandler(eq(COMMAND), eq(TestPayload.class.getName()));
        verify(objectMapper, times(1)).readValue(eq(message.getBody()), eq(TestPayload.class));
        verify(applicationContext, times(1)).getBean(eq(TestHandler.class));
    }

    @Test
    void routeMessage_shouldInvokeQueryHandler() throws Exception {
        Message message = createMessage(QUERY.name(), TestPayload.class.getName());
        TestHandler handler = new TestHandler();
        Method method = TestHandler.class.getMethod("handleQuery", TestPayload.class);

        when(handlerDiscover.getHandler(eq(QUERY), eq(TestPayload.class.getName())))
                .thenReturn(method);

        when(objectMapper.readValue(eq(message.getBody()), eq(TestPayload.class)))
                .thenReturn(new TestPayload());

        when(applicationContext.getBean(eq(TestHandler.class)))
                .thenReturn(handler);

        Object result = router.routeMessage(message);

        assertEquals("query handled", result);

        verify(handlerDiscover, times(1)).getHandler(eq(QUERY), eq(TestPayload.class.getName()));
        verify(objectMapper, times(1)).readValue(eq(message.getBody()), eq(TestPayload.class));
        verify(applicationContext, times(1)).getBean(eq(TestHandler.class));
    }

    @Test
    void routeMessage_shouldInvokeNotificationHandler() throws Exception {
        Message message = createMessage(NOTIFICATION.name(), TestPayload.class.getName());
        TestHandler handler = new TestHandler();
        Method method = TestHandler.class.getMethod("handleNotification", TestPayload.class);

        when(handlerDiscover.getHandler(eq(NOTIFICATION), eq(TestPayload.class.getName())))
                .thenReturn(method);

        when(objectMapper.readValue(eq(message.getBody()), eq(TestPayload.class)))
                .thenReturn(new TestPayload());

        when(applicationContext.getBean(eq(TestHandler.class)))
                .thenReturn(handler);

        Object result = router.routeMessage(message);

        assertNull(result);

        verify(handlerDiscover, times(1)).getHandler(eq(NOTIFICATION), eq(TestPayload.class.getName()));
        verify(objectMapper, times(1)).readValue(eq(message.getBody()), eq(TestPayload.class));
        verify(applicationContext, times(1)).getBean(eq(TestHandler.class));
    }

    @Test
    void routeMessage_shouldThrowTargetInvocationExceptionWhenInvokeHandler() throws Exception {
        Message message = createMessage(NOTIFICATION.name(), TestPayload.class.getName());
        TestHandlerWithException handler = new TestHandlerWithException();

        Method method = TestHandlerWithException.class.getMethod("handleNotification", TestPayload.class);

        when(handlerDiscover.getHandler(eq(NOTIFICATION), eq(TestPayload.class.getName())))
                .thenReturn(method);

        when(objectMapper.readValue(eq(message.getBody()), eq(TestPayload.class)))
                .thenReturn(new TestPayload());

        when(applicationContext.getBean(eq(TestHandlerWithException.class)))
                .thenReturn(handler);

        var ex = assertThrows(RuntimeException.class, () -> router.routeMessage(message));

        assertEquals(UnsupportedOperationException.class, ex.getCause().getClass());
        assertEquals("test exception", ex.getCause().getMessage());

        verify(handlerDiscover, times(1)).getHandler(eq(NOTIFICATION), eq(TestPayload.class.getName()));
        verify(objectMapper, times(1)).readValue(eq(message.getBody()), eq(TestPayload.class));
        verify(applicationContext, times(1)).getBean(eq(TestHandlerWithException.class));
    }

    @MessageListener
    static class TestHandlerWithException {
        @Notification
        public void handleNotification(TestPayload payload) {
            throw new UnsupportedOperationException("test exception");
        }
    }

    private Message createMessage(String messageType, String payloadClass) {
        MessageProperties props = new MessageProperties();
        Map<String, Object> headers = new HashMap<>();

        if (messageType != null) {
            headers.put(HANDLER_TYPE.getHeaderName(), messageType);
        }

        if (payloadClass != null) {
            headers.put(BODY_TYPE.getHeaderName(), payloadClass);
        }

        props.setHeaders(headers);

        return new Message("teste".getBytes(), props);
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