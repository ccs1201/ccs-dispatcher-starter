package br.com.messagedispatcher.util.validator;

import br.com.messagedispatcher.exceptions.MessageHandlerDuplicatedInputParameterException;
import br.com.messagedispatcher.exceptions.MessageHandlerMultipleInputParametersException;
import br.com.messagedispatcher.exceptions.MessageHandlerNoInputParameterException;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.util.HashMap;

import static br.com.messagedispatcher.constants.MessageDispatcherConstants.HandlerType;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class HandlerValidatorUtilTest {

    @SuppressWarnings("unused")
    static class DummyHandler {
        public void validHandler(String input) {}
        public void multipleParamsHandler(String input, int extra) {}
        public void noParamsHandler() {}
    }

    @Test
    void shouldThrowExceptionWhenMethodHasMultipleParameters() throws NoSuchMethodException {
        Method method = DummyHandler.class.getMethod("multipleParamsHandler", String.class, int.class);
        HashMap<String, Method> handlers = new HashMap<>();

        assertThrows(MessageHandlerMultipleInputParametersException.class, () ->
                HandlerValidatorUtil.validate(HandlerType.COMMAND, method, handlers)
        );
    }

    @Test
    void shouldThrowExceptionWhenMethodHasNoParameters() throws NoSuchMethodException {
        Method method = DummyHandler.class.getMethod("noParamsHandler");
        HashMap<String, Method> handlers = new HashMap<>();

        assertThrows(MessageHandlerNoInputParameterException.class, () ->
                HandlerValidatorUtil.validate(HandlerType.EVENT, method, handlers)
        );
    }

    @Test
    void shouldThrowExceptionWhenParameterTypeIsDuplicated() throws NoSuchMethodException {
        Method existingMethod = DummyHandler.class.getMethod("validHandler", String.class);
        Method duplicateMethod = DummyHandler.class.getMethod("validHandler", String.class);
        HashMap<String, Method> handlers = new HashMap<>();
        handlers.put("String", existingMethod);

        assertThrows(MessageHandlerDuplicatedInputParameterException.class, () ->
                HandlerValidatorUtil.validate(HandlerType.COMMAND, duplicateMethod, handlers)
        );
    }

    @Test
    void shouldPassValidationForValidHandler() throws NoSuchMethodException {
        Method method = DummyHandler.class.getMethod("validHandler", String.class);
        HashMap<String, Method> handlers = new HashMap<>();

        assertDoesNotThrow(() ->
                HandlerValidatorUtil.validate(HandlerType.EVENT, method, handlers)
        );
    }

}