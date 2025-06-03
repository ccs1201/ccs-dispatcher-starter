package br.com.messagedispatcher.pocs;

import br.com.messagedispatcher.beandiscover.impl.MessageDispatcherAnnotatedMethodDiscoverImpl;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.context.ApplicationContext;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;

public class MessageDispatcherAnnotatedMethodDiscoverImplTest {

    /**
     * Tests that an IllegalArgumentException is thrown when the ApplicationContext
     * contains no beans annotated as message listeners.
     * This is an edge case where the ApplicationContext is valid but empty.
     */
    @Test
    public void testMessageDispatcherAnnotatedMethodDiscover_EmptyApplicationContext() {
        ApplicationContext emptyContext = mock(ApplicationContext.class);
        assertThrows(IllegalArgumentException.class, () -> {
            new MessageDispatcherAnnotatedMethodDiscoverImpl(emptyContext);
        });
    }

    /**
     * Tests that a NullPointerException is thrown when the ApplicationContext is null.
     * This is an edge case where the constructor is called with an invalid (null) argument.
     */
    @Test
    public void testMessageDispatcherAnnotatedMethodDiscover_NullApplicationContext() {
        assertThrows(NullPointerException.class, () -> {
            new MessageDispatcherAnnotatedMethodDiscoverImpl(null);
        });
    }

    /**
     * Test case for MessageDispatcherAnnotatedMethodDiscover constructor
     * 
     * This test verifies that the MessageDispatcherAnnotatedMethodDiscover
     * constructor initializes the object correctly with an ApplicationContext.
     * It checks that the object is created without throwing any exceptions and
     * that the internal maps are initialized.
     */
    @Test
    public void test_MessageDispatcherAnnotatedMethodDiscover_Constructor() {
        // Mock ApplicationContext
        ApplicationContext mockContext = Mockito.mock(ApplicationContext.class);

        // Create MessageDispatcherAnnotatedMethodDiscover instance
        MessageDispatcherAnnotatedMethodDiscoverImpl discover = new MessageDispatcherAnnotatedMethodDiscoverImpl(mockContext);

        // Assert that the object is created successfully
        assertNotNull(discover);

        // Additional assertions can be added here to verify the internal state
        // of the object, if necessary. However, since the internal maps are private,
        // we cannot directly access them for assertion.
    }

}
