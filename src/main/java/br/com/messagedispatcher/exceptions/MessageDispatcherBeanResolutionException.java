package br.com.messagedispatcher.exceptions;

import org.springframework.beans.factory.BeanInitializationException;

public class MessageDispatcherBeanResolutionException extends BeanInitializationException {
    public MessageDispatcherBeanResolutionException(String message) {
        super(message);
    }
}
