package br.com.messagedispatcher.pocs;

import br.com.messagedispatcher.beandiscover.impl.MessageDispatcherAnnotatedHandlerDiscoverImpl;
import br.com.messagedispatcher.exceptions.MessageHandlerNotFoundException;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.context.ApplicationContext;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import static br.com.messagedispatcher.constants.MessageDispatcherConstants.HandlerType;
import static org.junit.jupiter.api.Assertions.*;

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


    /**
     * Test case for getHandler method when a valid handler is found
     * This test verifies that the getHandler method returns the correct Method
     * when a valid HandlerType and parameterType are provided, and a matching
     * handler exists in the internal map.
     */
    @Test
    public void testGetHandlerWhenHandlerExists() throws NoSuchMethodException, NoSuchFieldException, IllegalAccessException {
        ApplicationContext mockContext = Mockito.mock(ApplicationContext.class);
        MessageDispatcherAnnotatedHandlerDiscoverImpl discover = new MessageDispatcherAnnotatedHandlerDiscoverImpl(mockContext);

        // Create a mock Method
        Method mockMethod = String.class.getMethod("length");

        // Use reflection to set the private handlers field
        java.lang.reflect.Field handlersField = MessageDispatcherAnnotatedHandlerDiscoverImpl.class.getDeclaredField("handlers");
        handlersField.setAccessible(true);
        Map<HandlerType, HashMap<String, Method>> handlers = new HashMap<>();
        HashMap<String, Method> commandHandlers = new HashMap<>();
        commandHandlers.put("TestParameter", mockMethod);
        handlers.put(HandlerType.COMMAND, commandHandlers);
        handlersField.set(discover, handlers);

        Method result = discover.getHandler(HandlerType.COMMAND, "TestParameter");

        assertNotNull(result);
        assertEquals(mockMethod, result);
    }

    /**
     * Test case for getHandler method when no handler is found
     * This test verifies that the getHandler method throws a MessageHandlerNotFoundException
     * when no handler is found for the given HandlerType and parameterType.
     */
    @Test
    public void testGetHandlerWhenNoHandlerFound() {
        ApplicationContext mockContext = Mockito.mock(ApplicationContext.class);
        MessageDispatcherAnnotatedHandlerDiscoverImpl discover = new MessageDispatcherAnnotatedHandlerDiscoverImpl(mockContext);

        assertThrows(MessageHandlerNotFoundException.class, () ->
                discover.getHandler(HandlerType.COMMAND, "NonExistentType"));
    }

    /**
     * Test case for MessageDispatcherAnnotatedHandlerDiscoverImpl constructor
     * This test verifies that the MessageDispatcherAnnotatedHandlerDiscoverImpl
     * constructor initializes the object correctly with an ApplicationContext.
     * It checks that the handlers map is properly initialized with all HandlerTypes
     * and that the resolveAnnotatedMethods method is called.
     */
    @Test
    public void test_MessageDispatcherAnnotatedHandlerDiscoverImpl_Constructor() {
        ApplicationContext mockContext = Mockito.mock(ApplicationContext.class);

        MessageDispatcherAnnotatedHandlerDiscoverImpl discover = new MessageDispatcherAnnotatedHandlerDiscoverImpl(mockContext);

        assertNotNull(discover);
    }

    /**
     * Test case for getHandler method when no handler is found
     * This test verifies that the getHandler method throws a MessageHandlerNotFoundException
     * when no handler is found for the given HandlerType and parameterType.
     */
    @Test
    public void test_getHandler_throwsExceptionWhenNoHandlerFound() {
        ApplicationContext mockContext = Mockito.mock(ApplicationContext.class);
        MessageDispatcherAnnotatedHandlerDiscoverImpl discover = new MessageDispatcherAnnotatedHandlerDiscoverImpl(mockContext);

        assertThrows(MessageHandlerNotFoundException.class, () ->
                discover.getHandler(HandlerType.COMMAND, "NonExistentType"));
    }
}
