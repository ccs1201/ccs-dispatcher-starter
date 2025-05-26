package br.com.ccs.messagedispatcher.exceptions.handler;

import br.com.ccs.messagedispatcher.exceptions.MessageDispatcherRuntimeException;
import br.com.ccs.messagedispatcher.exceptions.handler.model.MessageExceptionModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class MessageDispatcherExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(MessageDispatcherExceptionHandler.class);

    @ExceptionHandler(MessageDispatcherRuntimeException.class)
    public MessageExceptionModel handle(MessageDispatcherRuntimeException e) {
        log.error(e.getMessage());
        return new MessageExceptionModel(e.getMessage(), e);
    }
}
