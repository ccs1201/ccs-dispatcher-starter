package br.com.messagedispatcher.util.validator;

import br.com.messagedispatcher.exceptions.MessageHandlerDuplicatedInputParameterException;
import br.com.messagedispatcher.exceptions.MessageHandlerMultipleInputParametersException;
import br.com.messagedispatcher.exceptions.MessageHandlerNoInputParameterException;

import java.lang.reflect.Method;
import java.util.HashMap;

import static br.com.messagedispatcher.constants.MessageDispatcherConstants.HandlerType;

public final class HandlerValidatorUtil {

    public static void validate(HandlerType handlerType, Method method, HashMap<String, Method> handlers) {
        String parameterType;
        try {
            parameterType = method.getParameterTypes()[0].getSimpleName();
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new MessageHandlerNoInputParameterException(String.format(
                    "Handler: @%s %s não possui parâmetros de entrada.", handlerType, method));
        }

        if (method.getParameterCount() > 1) {
            throw new MessageHandlerMultipleInputParametersException(String.format(
                    "Handler: @%s %s possui mais de um parâmetro de entrada.", handlerType, method));
        }

        if (handlers.containsKey(parameterType)) {
            throw new MessageHandlerDuplicatedInputParameterException(" Handler: @" + handlerType + " - " + handlers.get(parameterType).getName().toUpperCase()
                    + " na Classe: " + handlers.get(parameterType).getDeclaringClass().getName()
                    + " já declara o mesmo tipo de entrada que a Classe: " + method.getDeclaringClass().getName()
                    + " está declarando no Handler: " + method.getName().toUpperCase() + " para o Tipo de Entrada: " + parameterType.toUpperCase()
                    + " não são permitidos Handlers duplicados para o mesmo Tipo de Entrada.");

        }
    }
}
