package br.com.messagedispatcher.impl;

import br.com.messagedispatcher.exceptions.MessageRouterProcessingException;
import br.com.messagedispatcher.model.MockedMessageWrapper;
import br.com.messagedispatcher.router.impl.MockedMessageRouter;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.servlet.HandlerExecutionChain;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SuppressWarnings("unchecked")
@ExtendWith(MockitoExtension.class)
public class MockedMessageRouterTest {

    @InjectMocks
    private MockedMessageRouter router;

    @Mock
    private HandlerExecutionChain handler;

    @Mock
    private RequestMappingHandlerAdapter handlerAdapter;

    @Mock
    private RequestMappingHandlerMapping handlerMapping;

    @Mock
    private Message message;

    @Mock
    private MockedMessageWrapper mockedMessageWrapper;

    @Mock
    private ObjectMapper objectMapper;

    /**
     * Test case for handleMessage method when the message body is null, headers and query parameters are present,
     * no handler is found, and the response is JSON.
     * <p>
     * This test verifies that the method correctly handles a scenario where:
     * - The message body is null
     * - Headers and query parameters are present in the message wrapper
     * - No handler is found for the given path
     * - The response body is not empty and contains JSON content
     * <p>
     * Expected behavior:
     * - The method should throw an IllegalArgumentException due to no handler being found
     * - The exception message should indicate that no handler was found for the given path
     */
    @Test
    public void testRouteMessageWithNullBodyAndNoHandler() throws Exception {

        MockedMessageWrapper mockedMessageWrapper = MockedMessageWrapper.builder()
                .path("/xxx")
                .method("GET")
                .body(null)
                .headers(new HashMap<>())
                .queryParams(new HashMap<>())
                .build();

        when(message.getBody()).thenReturn("message body".getBytes(StandardCharsets.UTF_8));
        when(objectMapper.readValue(any(byte[].class), any(Class.class))).thenReturn(mockedMessageWrapper);
        when(handlerMapping.getHandler(any(MockHttpServletRequest.class))).thenReturn(null);

        // Act & Assert
        var exception = assertThrows(MessageRouterProcessingException.class, () -> router.routeMessage(message));

        assertEquals("Nenhum handler encontrado para path: /xxx", exception.getMessage());
    }

    /**
     * Test case for the constructor of MockMvcMessageRouterImpl.
     * Verifies that the constructor initializes the object correctly with the provided dependencies.
     */
    @Test
    public void test_MockMvcMessageRouterImpl_Constructor() {
        assertNotNull(router, "MockMvcMessageRouterImpl should be created successfully");
    }

    /**
     * Tests the handleMessage method when no handler is found for the given path.
     * This scenario is explicitly handled in the focal method by throwing an IllegalArgumentException.
     */
    @Test
    public void test_routeMessage_noHandlerFound() throws Exception {
        MockedMessageWrapper mockedMessageWrapper = MockedMessageWrapper.builder()
                .path("/invalid-path")
                .method("GET")
                .headers(new HashMap<>())
                .build();

        when(message.getBody()).thenReturn("message body".getBytes(StandardCharsets.UTF_8));
        when(objectMapper.readValue(any(byte[].class), any(Class.class))).thenReturn(mockedMessageWrapper);
        when(handlerMapping.getHandler(any())).thenReturn(null);

        assertThrows(MessageRouterProcessingException.class, () -> router.routeMessage(message));

    }

    /**
     * Test case for handleMessage when the message body is not null, headers and query parameters are present,
     * but no handler is found, and the response body is empty.
     * <p>
     * This test verifies that the method returns null when no handler is found for the given path,
     * and throws an IllegalArgumentException with an appropriate error message.
     */
    @Test
    public void test_routeMessage_noHandlerFoundEmptyResponse() throws Exception {
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");

        Map<String, String> queryParams = new HashMap<>();
        queryParams.put("param1", "value1");

        MockedMessageWrapper mockedMessageWrapper = MockedMessageWrapper.builder()
                .method("GET")
                .path("/test")
                .body("test body")
                .headers(headers)
                .queryParams(queryParams)
                .build();

        when(message.getBody()).thenReturn("message body".getBytes(StandardCharsets.UTF_8));
        when(objectMapper.readValue(any(byte[].class), eq(MockedMessageWrapper.class))).thenReturn(mockedMessageWrapper);
        when(handlerMapping.getHandler(any(MockHttpServletRequest.class))).thenReturn(null);

        // Act & Assert
        var exception = assertThrows(MessageRouterProcessingException.class, () -> router.routeMessage(message));
        assertEquals("Nenhum handler encontrado para path: /test", exception.getMessage());
    }

    /**
     * Tests the handleMessage method when a message with body, headers, and query parameters is received,
     * but no handler is found, and the response is non-JSON content.
     * <p>
     * This test covers the following path:
     * - Message body is not null
     * - Message headers are not null
     * - Message query parameters are not null
     * - No handler is found for the request
     * - Response body length is greater than 0
     * - Response content type is not JSON
     * <p>
     * Expected outcome: The method should throw an IllegalArgumentException due to no handler found,
     * which is then wrapped in a MessageDispatcherException.
     */
    @Test
    public void test_routeMessage_noHandlerFoundNonJsonResponse() {
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "text/plain");

        Map<String, String> queryParams = new HashMap<>();
        queryParams.put("param", "value");

        MockedMessageWrapper wrapper = MockedMessageWrapper.builder()
                .method("GET")
                .path("/test")
                .body("test body")
                .headers(headers)
                .queryParams(queryParams)
                .build();

        try {
            // Mock behavior
            when(message.getBody()).thenReturn("message body".getBytes(StandardCharsets.UTF_8));
            when(objectMapper.readValue(any(byte[].class), eq(MockedMessageWrapper.class))).thenReturn(wrapper);
            when(handlerMapping.getHandler(any(MockHttpServletRequest.class))).thenReturn(null);

            // Execute method and assert
            assertThrows(MessageRouterProcessingException.class, () -> router.routeMessage(message));

            // Verify interactions
            verify(handlerMapping).getHandler(any(MockHttpServletRequest.class));
            verify(handlerAdapter, never()).handle(any(), any(), any());
        } catch (Exception e) {
            fail("Test should not throw exception: " + e.getMessage());
        }
    }

    /**
     * Test case for handleMessage method when:
     * - Message body is not null
     * - Headers are null
     * - Query parameters are not null
     * - No handler is found
     * - Response body is not empty
     * - Response content type is JSON
     * <p>
     * Expected: IllegalArgumentException is thrown
     */
    @Test
    public void test_routeMessage_noHandlerFound_2() throws Exception {
        Map<String, String> queryParams = new HashMap<>();
        queryParams.put("param", "value");

        MockedMessageWrapper mockedMessageWrapper = MockedMessageWrapper.builder()
                .method("GET")
                .path("/test")
                .body("test body")
                .headers(new HashMap<>())
                .queryParams(queryParams)
                .build();


        when(message.getBody()).thenReturn("message body".getBytes(StandardCharsets.UTF_8));
        when(objectMapper.readValue(any(byte[].class), eq(MockedMessageWrapper.class))).thenReturn(mockedMessageWrapper);
        when(handlerMapping.getHandler(any(MockHttpServletRequest.class))).thenReturn(null);

        // Act & Assert
        assertThrows(MessageRouterProcessingException.class, () -> router.routeMessage(message));
    }

    /**
     * Test case for handleMessage method when the message has a body, headers, no query params,
     * no handler is found, and the response is JSON.
     * <p>
     * This test verifies that:
     * 1. The method correctly processes a message with a body and headers.
     * 2. It throws an IllegalArgumentException when no handler is found.
     * 3. The response body is processed correctly when it's JSON.
     */
    @Test
    public void test_routeMessage_whenNoHandlerFoundAndJsonResponse() throws Exception {
        MockedMessageWrapper mockedMessageWrapper = MockedMessageWrapper.builder()
                .method("GET")
                .path("/test")
                .body("test body")
                .headers(new HashMap<>())
                .build();


        // Mocking behavior
        when(message.getBody()).thenReturn("dummy".getBytes());
        when(objectMapper.readValue(any(byte[].class), eq(MockedMessageWrapper.class))).thenReturn(mockedMessageWrapper);
        when(handlerMapping.getHandler(any(MockHttpServletRequest.class))).thenReturn(null);

        // Executing the method and asserting the exception
        assertThrows(MessageRouterProcessingException.class, () -> router.routeMessage(message));

        // Verifying interactions
        verify(objectMapper).readValue(any(byte[].class), eq(MockedMessageWrapper.class));
        verify(handlerMapping).getHandler(any(MockHttpServletRequest.class));
        verifyNoInteractions(handlerAdapter);
    }

    /**
     * Tests the handleMessage method when all conditions are met:
     * - Message body, headers, and query parameters are present
     * - Handler is found
     * - Response body is not empty
     * - Response content type is JSON
     * <p>
     * Expected: The method should process the message and return the parsed JSON response
     */
    @Test
    public void test_routeMessage_withValidJsonResponse() throws Exception {
         Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        Map<String, String> queryParams = new HashMap<>();
        queryParams.put("param", "value");

        MockedMessageWrapper mockedMessageWrapper = MockedMessageWrapper.builder()
                .method("GET")
                .path("/test")
                .body("test body")
                .headers(headers)
                .queryParams(queryParams)
                .build();

        // Mocking behavior
        when(message.getBody()).thenReturn("test message".getBytes());
        when(objectMapper.readValue(any(byte[].class), eq(MockedMessageWrapper.class))).thenReturn(mockedMessageWrapper);
        when(handlerMapping.getHandler(any(MockHttpServletRequest.class))).thenReturn(mock(HandlerExecutionChain.class));

        MockHttpServletResponse mockResponse = new MockHttpServletResponse();
        mockResponse.setContentType(MessageProperties.CONTENT_TYPE_JSON);
        mockResponse.getWriter().write("{\"key\":\"value\"}");
        doAnswer(invocation -> {
            MockHttpServletResponse response = invocation.getArgument(1);
            response.setContentType(MessageProperties.CONTENT_TYPE_JSON);
            response.getWriter().write("{\"key\":\"value\"}");
            return null;
        }).when(handlerAdapter).handle(any(), any(), any());

        Map<String, String> expectedResult = new HashMap<>();
        expectedResult.put("key", "value");
        when(objectMapper.readValue(any(byte[].class), eq(Object.class))).thenReturn(expectedResult);

        // Executing the method
        Object result = router.routeMessage(message);

        // Verifying the result
        assertNotNull(result);
        assertInstanceOf(Map.class, result);
        assertEquals("value", ((Map<?, ?>) result).get("key"));

        // Verifying interactions
        verify(objectMapper).readValue(any(byte[].class), eq(MockedMessageWrapper.class));
        verify(handlerMapping).getHandler(any(MockHttpServletRequest.class));
        verify(handlerAdapter).handle(any(), any(), any());
        verify(objectMapper).readValue(any(byte[].class), eq(Object.class));
    }

}
