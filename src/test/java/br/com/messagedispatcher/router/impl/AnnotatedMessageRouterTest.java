package br.com.messagedispatcher.router.impl;

import br.com.messagedispatcher.annotation.Command;
import br.com.messagedispatcher.annotation.MessageListener;
import br.com.messagedispatcher.annotation.Notification;
import br.com.messagedispatcher.annotation.Query;
import br.com.messagedispatcher.beandiscover.MessageDispatcherAnnotatedHandlerDiscover;
import br.com.messagedispatcher.exceptions.MessageHandlerNotFoundException;
import br.com.messagedispatcher.exceptions.MessageRouterMissingHeaderException;
import br.com.messagedispatcher.util.context.MessageDispatcherContextHolder;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
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

    @AfterEach
    void tearDown() {
        MessageDispatcherContextHolder.clear();
    }

    @Test
    void routeMessageShouldThrowExceptionWhenMessageTypeHeaderIsMissing() {
        Message message = createMessage(null, TestPayload.class.getName());

        assertThrows(MessageRouterMissingHeaderException.class, () -> router.routeMessage(message));

        verifyNoInteractions(handlerDiscover);
        verifyNoInteractions(objectMapper);
        verifyNoInteractions(applicationContext);
    }

    @Test
    void routeMessageShouldThrowExceptionWhenPayloadClassHeaderIsMissing() {
        Message message = createMessage(COMMAND.name(), null);

        assertThrows(MessageRouterMissingHeaderException.class, () -> router.routeMessage(message));

        verifyNoInteractions(handlerDiscover);
        verifyNoInteractions(objectMapper);
        verifyNoInteractions(applicationContext);
    }

    @Test
    void routeMessageShouldThrowExceptionWhenHandlerNotFound() {
        Message message = createMessage(COMMAND.name(), TestPayload.class.getName());
        when(handlerDiscover.getHandler(any(), any())).thenThrow(MessageHandlerNotFoundException.class);

        assertThrows(RuntimeException.class, () -> router.routeMessage(message));

        verify(handlerDiscover, times(1)).getHandler(eq(COMMAND), eq(TestPayload.class.getName()));
        verifyNoInteractions(objectMapper);
        verifyNoInteractions(applicationContext);
    }

    @Test
    void routeMessageShouldInvokeCommandHandler() throws Exception {
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
    void routeMessageShouldInvokeQueryHandler() throws Exception {
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
    void routeMessageShouldInvokeNotificationHandler() throws Exception {
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
    void routeMessageShouldThrowTargetInvocationExceptionWhenHandlerThrowsException() throws Exception {
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

    @Test
    void routeMessageShouldSetHeadersInContextHolder() throws Exception {
        Map<String, Object> customHeaders = new HashMap<>();
        customHeaders.put(HANDLER_TYPE.getHeaderName(), COMMAND.name());
        customHeaders.put(BODY_TYPE.getHeaderName(), TestPayload.class.getName());
        customHeaders.put("X-Custom-Header", "custom-value");
        customHeaders.put("X-Correlation-ID", "123456");
        
        Message message = createMessageWithCustomHeaders(customHeaders);
        TestHandler handler = new TestHandler();
        Method method = TestHandler.class.getMethod("handleCommand", TestPayload.class);

        when(handlerDiscover.getHandler(eq(COMMAND), eq(TestPayload.class.getName())))
                .thenReturn(method);

        when(objectMapper.readValue(eq(message.getBody()), eq(TestPayload.class)))
                .thenReturn(new TestPayload());

        when(applicationContext.getBean(eq(TestHandler.class)))
                .thenReturn(handler);

        router.routeMessage(message);

        verify(handlerDiscover, times(1)).getHandler(eq(COMMAND), eq(TestPayload.class.getName()));
    }

    @Test
    void routeMessageShouldClearHeadersInContextHolderAfterExecution() throws Exception {
        Message message = createMessage(COMMAND.name(), TestPayload.class.getName());
        TestHandler handler = new TestHandler();
        Method method = TestHandler.class.getMethod("handleCommand", TestPayload.class);

        when(handlerDiscover.getHandler(eq(COMMAND), eq(TestPayload.class.getName())))
                .thenReturn(method);

        when(objectMapper.readValue(eq(message.getBody()), eq(TestPayload.class)))
                .thenReturn(new TestPayload());

        when(applicationContext.getBean(eq(TestHandler.class)))
                .thenReturn(handler);

        router.routeMessage(message);

        assertNull(MessageDispatcherContextHolder.getHeaders());
    }

    @Test
    void routeMessageShouldClearHeadersInContextHolderEvenWhenExceptionIsThrown() {
        Message message = createMessage(COMMAND.name(), TestPayload.class.getName());
        when(handlerDiscover.getHandler(any(), any())).thenThrow(new RuntimeException("Test exception"));

        assertThrows(RuntimeException.class, () -> router.routeMessage(message));

        assertNull(MessageDispatcherContextHolder.getHeaders());
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

    private Message createMessageWithCustomHeaders(Map<String, Object> headers) {
        MessageProperties props = new MessageProperties();
        props.setHeaders(headers);
        return new Message("teste".getBytes(), props);
    }

    @SuppressWarnings("unused")
    @MessageListener
    static class TestHandler {
        @Command
        public String handleCommand(TestPayload payload) {
            Map<String, Object> headers = MessageDispatcherContextHolder.getHeaders();
            if (headers != null && headers.containsKey("X-Custom-Header")) {
                return "command handled with headers";
            }
            return "command handled";
        }

        @Query
        public String handleQuery(TestPayload payload) {
            return "query handled";
        }

        @Notification
        public void handleNotification(TestPayload payload) {
        }
    }

    @SuppressWarnings({"FieldCanBeLocal", "FieldMayBeFinal", "unused"})
    static class TestPayload {
        private String data = "test";

        public String getData() {
            return data;
        }
    }
}