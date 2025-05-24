package br.com.ccs.dispatcher.http.impl;

import br.com.ccs.messagedispatcher.http.impl.HttpRequestClientImpl;
import br.com.ccs.messagedispatcher.messaging.exceptions.HttpRequestClientExceptionMessage;
import br.com.ccs.messagedispatcher.messaging.model.MessageWrapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.web.context.WebServerInitializedEvent;
import org.springframework.boot.web.server.WebServer;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HttpRequestClientImplTest {

    @InjectMocks
    private HttpRequestClientImpl httpRequestClient;

    @Mock
    private HttpClient mockHttpClient;

    @Mock
    private HttpResponse<String> mockResponse;

    @Mock
    private MessageWrapper messageWrapper;

    @Mock
    private WebServerInitializedEvent mockEvent;

    @Mock
    private WebServer mockWebServer;

    @Mock
    private ObjectMapper objectMapper;

    /**
     * Tests the doRequest method when an IOException occurs during the HTTP request.
     * This test ensures that the method properly handles IO-related exceptions by wrapping them in a RuntimeException.
     */
    @Test
    public void test_doRequest_handleIOException() throws Exception {
        // Arrange
        when(messageWrapper.getPath()).thenReturn("/test");
        when(messageWrapper.getMethod()).thenReturn("GET"); // Adicionando mock do método
        when(messageWrapper.getHeaders()).thenReturn(Map.of()); // Adicionando mock dos headers
        when(mockHttpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                .thenThrow(new IOException("IO Exception"));

        HttpClient.Builder mockBuilder = mock(HttpClient.Builder.class);

        // Configure BASE_URL using reflection
        java.lang.reflect.Field baseUrlField = HttpRequestClientImpl.class.getDeclaredField("BASE_URL");
        baseUrlField.setAccessible(true);
        baseUrlField.set(httpRequestClient, new URI("http://localhost:8080"));

        try (var mockedStatic = mockStatic(HttpClient.class)) {
            // Mock the builder chain
            mockedStatic.when(HttpClient::newBuilder).thenReturn(mockBuilder);
            when(mockBuilder.version(any(HttpClient.Version.class))).thenReturn(mockBuilder);
            when(mockBuilder.build()).thenReturn(mockHttpClient);

            // Act & Assert
            assertThrows(HttpRequestClientExceptionMessage.class, () -> httpRequestClient.doRequest(messageWrapper));
        }

        // Verify
        verify(messageWrapper, times(3)).getPath();
        verify(messageWrapper, times(1)).getMethod();
        verify(mockHttpClient).send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class));
    }


    /**
     * Tests the doRequest method when an InterruptedException occurs during the HTTP request.
     * This test verifies that the method correctly handles the exception by wrapping it in a RuntimeException.
     */
    @Test
    public void test_doRequest_handleInterruptedException() throws Exception {
        lenient().when(messageWrapper.getPath()).thenReturn("/test");
        lenient().when(messageWrapper.getMethod()).thenReturn("GET");
        lenient().when(mockHttpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                .thenThrow(new InterruptedException("Test interruption"));

        HttpClient.Builder mockBuilder = mock(HttpClient.Builder.class);

        java.lang.reflect.Field baseUrlField = HttpRequestClientImpl.class.getDeclaredField("BASE_URL");
        baseUrlField.setAccessible(true);
        baseUrlField.set(httpRequestClient, new URI("http://localhost:8080"));

        try (var mockedStatic = mockStatic(HttpClient.class)) {
            mockedStatic.when(HttpClient::newBuilder).thenReturn(mockBuilder);
            when(mockBuilder.version(any(HttpClient.Version.class))).thenReturn(mockBuilder);
            when(mockBuilder.build()).thenReturn(mockHttpClient);

            assertThrows(HttpRequestClientExceptionMessage.class, () -> httpRequestClient.doRequest(messageWrapper));
        }
    }


    /**
     * Tests the doRequest method of HttpRequestClientImpl.
     * Verifies that the method correctly sends an HTTP request and returns the response body.
     */
    @Test
    public void test_doRequest_returnsHttpResponseBody() throws Exception {
        MessageWrapper messageWrapper = MessageWrapper.builder()
                .path("/test")
                .method("GET")
                .headers(Map.of("a", "b"))
                .build();

        when(mockResponse.body()).thenReturn("Test response");
        when(mockHttpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                .thenReturn(mockResponse);

        HttpClient.Builder mockBuilder = mock(HttpClient.Builder.class);

        try (var mockedStatic = mockStatic(HttpClient.class)) {
            // Mock the builder chain
            mockedStatic.when(HttpClient::newBuilder).thenReturn(mockBuilder);
            when(mockBuilder.version(any(HttpClient.Version.class))).thenReturn(mockBuilder);
            when(mockBuilder.build()).thenReturn(mockHttpClient);

            // Use reflection to set the BASE_URL field
            java.lang.reflect.Field baseUrlField = HttpRequestClientImpl.class.getDeclaredField("BASE_URL");
            baseUrlField.setAccessible(true);
            baseUrlField.set(httpRequestClient, new URI("http://localhost:8080"));

            Object result = httpRequestClient.doRequest(messageWrapper);

            assertEquals("Test response", result);
        }
    }

    /**
     * Test case for onApplicationEvent method.
     * Verifies that the BASE_URL is correctly set when the WebServerInitializedEvent is received.
     */
    @Test
    public void test_onApplicationEvent_setsBaseUrlCorrectly() {
        when(mockEvent.getWebServer()).thenReturn(mockWebServer);
        when(mockWebServer.getPort()).thenReturn(8080);

        httpRequestClient.onApplicationEvent(mockEvent);

        try {
            java.lang.reflect.Field baseUrlField = HttpRequestClientImpl.class.getDeclaredField("BASE_URL");
            baseUrlField.setAccessible(true);
            java.net.URI baseUrl = (java.net.URI) baseUrlField.get(httpRequestClient);

            assertEquals("http://localhost:8080", baseUrl.toString());
        } catch (NoSuchFieldException | IllegalAccessException e) {
            fail("Failed to access BASE_URL field: " + e.getMessage());
        }
    }
}
