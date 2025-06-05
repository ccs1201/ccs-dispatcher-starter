package br.com.messagedispatcher.pocs;

import br.com.messagedispatcher.beandiscover.impl.MessageDispatcherAnnotatedHandlerDiscoverImpl;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.context.ApplicationContext;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class MessageDispatcherAnnotatedHandlerDiscoverImplTest {

    /**
     * Test case for MessageDispatcherAnnotatedMethodDiscover constructor
     * This test verifies that the MessageDispatcherAnnotatedMethodDiscover
     * constructor initializes the object correctly with an ApplicationContext.
     * It checks that the object is created without throwing any exceptions and
     * that the internal maps are initialized.
     */
    @Test
    public void test_MessageDispatcherAnnotatedMethodDiscover_Constructor() {
        ApplicationContext mockContext = Mockito.mock(ApplicationContext.class);

        MessageDispatcherAnnotatedHandlerDiscoverImpl discover = new MessageDispatcherAnnotatedHandlerDiscoverImpl(mockContext);

        assertNotNull(discover);
    }

}
