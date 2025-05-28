package br.com.ccs.messagedispatcher.exceptions.handler;

import br.com.ccs.messagedispatcher.exceptions.MessageDispatcherRemoteProcessException;
import br.com.ccs.messagedispatcher.exceptions.MessagePublisherTimeOutException;
import br.com.ccs.messagedispatcher.util.EnvironmentUtils;
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
    public MessageDispatcherProblemDetailExceptionResponse handle(MessageDispatcherRemoteProcessException e) {
        log.error("Ocorreu um erro no processamento remoto Message: {} Cause: {}", e.getMessage(), e.getRemoteCause());
        return buildProblemDetailExceptionResponse(e.getStatus(), e.getMessage(), e.getOriginService());
    }

    @ExceptionHandler(MessagePublisherTimeOutException.class)
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    public MessageDispatcherProblemDetailExceptionResponse handle(MessagePublisherTimeOutException e) {
        log.error("\"Ocorreu um erro no processamento remoto Message: {}", e.getMessage());
        return buildProblemDetailExceptionResponse(e.getStatus(), e.getMessage(), EnvironmentUtils.getAppName());
    }

    private static MessageDispatcherProblemDetailExceptionResponse buildProblemDetailExceptionResponse(HttpStatus e, String message, String orinService) {
        return MessageDispatcherProblemDetailExceptionResponse.of(e.name(), e.value(), message, orinService);
    }

}
