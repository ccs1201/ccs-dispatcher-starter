package br.com.ccs.messagedispatcher.exceptions.handler;

import br.com.ccs.messagedispatcher.exceptions.MessageDispatcherRemoteProcessException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class MessageDispatcherExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(MessageDispatcherExceptionHandler.class);

    @ExceptionHandler(MessageDispatcherRemoteProcessException.class)
    public ProblemDetail handle(MessageDispatcherRemoteProcessException e) {
        log.error(e.getMessage());
        return ProblemDetail.forStatusAndDetail(e.getStatus(),
                e.getMessage().replaceAll("br.com.ccs.messagedispatcher.exceptions.MessageDispatcherRemoteProcessException: ", ""));
    }
}
