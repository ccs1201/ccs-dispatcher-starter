package br.com.messagedispatcher.util.httpservlet;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class RequestContextUtilTest {

    @Mock
    private ServletRequestAttributes servletRequestAttributes;

    /**
     * Tests the getCurrentRequest method when RequestContextHolder has no request attribute set.
     * It verifies that the method returns null when there's no current request.
     */
    @Test
    public void testGetCurrentRequestWhenNoRequestAttributeIsSet() {
        RequestContextHolder.resetRequestAttributes();

        assertNull(RequestContextUtil.getCurrentRequest());
    }

    /**
     * Tests the getCurrentRequest method when RequestContextHolder has a request attribute set.
     * It verifies that the method returns a non-null HttpServletRequest object.
     */
    @Test
    public void testGetCurrentRequestWhenRequestAttributeIsSet() {
        MockHttpServletRequest mockRequest = new MockHttpServletRequest();
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(mockRequest));

        assertNotNull(RequestContextUtil.getCurrentRequest());

        RequestContextHolder.resetRequestAttributes();
    }

    /**
     * Tests the getCurrentRequest method when no request attributes are set.
     * This is a negative test case that verifies the method returns null
     * when the RequestContextHolder has no request attributes.
     */
    @Test
    public void testGetCurrentRequestWithNoRequestAttributes() {
        // Ensure no request attributes are set
        RequestContextHolder.resetRequestAttributes();

        // Call the method under test
        var result = RequestContextUtil.getCurrentRequest();

        // Assert that the result is null
        assertNull(result, "getCurrentRequest should return null when no request attributes are set");
    }

    /**
     * Tests the behavior of getHeader method when no request context is available.
     * This scenario verifies that an empty map is returned when RequestContextHolder
     * does not contain any request attributes.
     */
    @Test
    public void testGetHeaderWhenNoRequestContextAvailable() {
        // Clear any existing request attributes
        RequestContextHolder.resetRequestAttributes();

        // Call the method under test
        var result = RequestContextUtil.getHeader("SomeHeader");

        // Verify that an empty map is returned
        assertTrue(result.isEmpty(), "Expected an empty map when no request context is available");
    }

    /**
     * Tests the behavior of getHeaders() when there is no current request context.
     * This scenario is explicitly handled in the method by returning an empty map
     * when getCurrentRequest() returns null.
     */
    @Test
    public void testGetHeadersWithNoRequestContext() {
        // Ensure there's no request context
        RequestContextHolder.resetRequestAttributes();

        // Call the method under test
        Map<String, String> headers = RequestContextUtil.getHeaders();

        // Assert that the result is an empty map
        assertTrue(headers.isEmpty(), "Headers should be empty when there's no request context");
    }

    /**
     * Tests the getHeaders method when a valid request is present.
     * Verifies that all headers from the request are correctly retrieved and returned.
     */
    @Test
    public void testGetHeadersWithValidRequest() {
        MockHttpServletRequest mockRequest = new MockHttpServletRequest();
        mockRequest.addHeader("Content-Type", "application/json");
        mockRequest.addHeader("Authorization", "Bearer token123");

        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(mockRequest));

        Map<String, String> headers = RequestContextUtil.getHeaders();

        assertNotNull(headers);
        assertEquals(2, headers.size());
        assertEquals("application/json", headers.get("Content-Type"));
        assertEquals("Bearer token123", headers.get("Authorization"));

        RequestContextHolder.resetRequestAttributes();
    }

    /**
     * Tests that getMethod() returns the correct HTTP method when a valid request is present.
     * This test sets up a mock HTTP request with the GET method and verifies that
     * getMethod() correctly retrieves and returns this method.
     */
    @Test
    public void testGetMethodReturnsCorrectMethodForValidRequest() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setMethod("GET");
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

        String result = RequestContextUtil.getMethod();

        assertEquals("GET", result);
        RequestContextHolder.resetRequestAttributes();
    }

    /**
     * Tests the behavior of getPath() when there is no current request.
     * This is an edge case where RequestContextHolder returns null, which is
     * explicitly handled in the getPath() method implementation.
     */
    @Test
    public void testGetPathWhenNoCurrentRequest() {
        // Ensure there's no current request
        RequestContextHolder.resetRequestAttributes();

        // Call the method under test
        String result = RequestContextUtil.getPath();

        // Verify that an empty string is returned when there's no current request
        assertEquals("", result);
    }

    /**
     * Tests the behavior of getQueryParam when the current request is not available.
     * This scenario is explicitly handled in the method by returning an empty string
     * when the request object is null.
     */
    @Test
    public void testGetQueryParamWithNoCurrentRequest() {
        // Ensure there's no current request set
        RequestContextHolder.resetRequestAttributes();

        // Call the method under test
        String result = RequestContextUtil.getQueryParam("anyParam");

        // Verify that an empty string is returned when there's no current request
        assertEquals("", result);
    }

    /**
     * Tests the behavior of getQueryParams() when there is no current request.
     * This test verifies that the method returns an empty map when the RequestContextHolder
     * does not have a current request set.
     */
    @Test
    public void testGetQueryParamsWithNoCurrentRequest() {
        // Ensure there's no current request set
        RequestContextHolder.resetRequestAttributes();

        // Call the method under test
        Map<String, String> result = RequestContextUtil.getQueryParams();

        // Verify that an empty map is returned
        assertTrue(result.isEmpty());
    }

    /**
     * Tests the getHeader method when a valid request is present.
     * It verifies that the method returns a map containing the specified header name and value.
     */
    @SuppressWarnings("OptionalGetWithoutIsPresent")
    @Test
    public void test_getHeader_whenRequestExists() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Test-Header", "Test-Value");
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

        var result = RequestContextUtil.getHeader("Test-Header");

        assertEquals("Test-Value", result.get());

        RequestContextHolder.resetRequestAttributes();
    }

    /**
     * Tests the getHeader method when the current request is null.
     * Expects an empty map to be returned.
     */
    @Test
    public void test_getHeader_whenRequestIsNull() {
        String headerName = "Test-Header";
        var result = RequestContextUtil.getHeader(headerName);
        assertTrue(result.isEmpty(), "The result should be an empty map when the request is null");
    }

    /**
     * Tests the getMethod() method when no HttpServletRequest is available.
     * This test verifies that an empty string is returned when the RequestContextHolder
     * does not have a current request set.
     */
    @Test
    public void test_getMethod_whenNoRequestAvailable_returnsEmptyString() {
        // Ensure no request is set in the RequestContextHolder
        RequestContextHolder.resetRequestAttributes();

        // Call the method under test
        String result = RequestContextUtil.getMethod();

        // Verify that an empty string is returned
        assertEquals("", result);
    }

    /**
     * Test case for getPath() method when the request is not null.
     * It verifies that the method returns the correct request URI.
     */
    @Test
    public void test_getPath_whenRequestIsNotNull() {
        MockHttpServletRequest mockRequest = new MockHttpServletRequest();
        mockRequest.setRequestURI("/test/path");
        when(servletRequestAttributes.getRequest()).thenReturn(mockRequest);
        RequestContextHolder.setRequestAttributes(servletRequestAttributes);

        String result = RequestContextUtil.getPath();

        assertEquals("/test/path", result);

        RequestContextHolder.resetRequestAttributes();
    }

    /**
     * Tests the getQueryParam method when a valid request is present.
     * It verifies that the method correctly retrieves the parameter value
     * from the request object.
     */
    @Test
    public void test_getQueryParam_whenRequestExists() {
        // Arrange
        MockHttpServletRequest mockRequest = new MockHttpServletRequest();
        mockRequest.setParameter("testParam", "testValue");
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(mockRequest));

        // Act
        String result = RequestContextUtil.getQueryParam("testParam");

        // Assert
        assertEquals("testValue", result);

        // Cleanup
        RequestContextHolder.resetRequestAttributes();
    }

    /**
     * Tests the getQueryParams method when a valid request is present.
     * It verifies that the method correctly retrieves and returns all query parameters.
     */
    @Test
    public void test_getQueryParams_whenRequestIsPresent() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setParameter("param1", "value1");
        request.setParameter("param2", "value2");
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

        Map<String, String> result = RequestContextUtil.getQueryParams();

        assertEquals(2, result.size());
        assertEquals("value1", result.get("param1"));
        assertEquals("value2", result.get("param2"));

        RequestContextHolder.resetRequestAttributes();
    }

}
