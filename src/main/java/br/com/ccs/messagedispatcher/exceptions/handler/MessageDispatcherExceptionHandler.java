package br.com.ccs.messagedispatcher.exceptions.handler;

import br.com.ccs.messagedispatcher.exceptions.MessageDispatcherRemoteProcessException;
import br.com.ccs.messagedispatcher.exceptions.MessagePublisherTimeOutException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@SuppressWarnings("unused")
public class MessageDispatcherExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(MessageDispatcherExceptionHandler.class);

    @ExceptionHandler(MessageDispatcherRemoteProcessException.class)
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    public ProblemDetailExceptionResponse handle(MessageDispatcherRemoteProcessException e) {
        log.error("Ocorreu um erro no processamento remoto Message: {} Cause: {}", e.getMessage(), e.getRemoteCause());
        return ProblemDetailExceptionResponse.of(e.getStatus().name(), e.getStatus().value(), "Serviço: " + e.getMessage());
    }

    @ExceptionHandler(MessagePublisherTimeOutException.class)
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    public ProblemDetailExceptionResponse handle(MessagePublisherTimeOutException e) {
        log.error("\"Ocorreu um erro no processamento remoto Message: {}", e.getMessage());
        return ProblemDetailExceptionResponse.of(e.getStatus().name(), e.getStatus().value(), "Serviço: " + e.getMessage());
    }

}
