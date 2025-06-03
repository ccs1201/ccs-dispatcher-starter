package br.com.messagedispatcher.exceptions.handler;

import br.com.messagedispatcher.exceptions.MessageDispatcherRemoteProcessException;
import br.com.messagedispatcher.exceptions.MessagePublisherTimeOutException;
import br.com.messagedispatcher.util.EnvironmentUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@SuppressWarnings("unused")
public class MessageDispatcherExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(MessageDispatcherExceptionHandler.class);

    @ExceptionHandler(MessageDispatcherRemoteProcessException.class)
    public ResponseEntity<MessageDispatcherProblemDetailExceptionResponse> handle(MessageDispatcherRemoteProcessException e) {
        log.error("Ocorreu um erro no processamento remoto Message: {} Cause: {}", e.getMessage(), e.getRemoteCause());
        return buildProblemDetailExceptionResponse(e.getStatus(), e.getMessage(), e.getOriginService());
    }

    @ExceptionHandler(MessagePublisherTimeOutException.class)
    public ResponseEntity<MessageDispatcherProblemDetailExceptionResponse> handle(MessagePublisherTimeOutException e) {
        log.error("\"Ocorreu um erro no processamento remoto Message: {}", e.getMessage());
        return buildProblemDetailExceptionResponse(e.getStatus(), e.getMessage(), EnvironmentUtils.getAppName());
    }

    private static ResponseEntity<MessageDispatcherProblemDetailExceptionResponse> buildProblemDetailExceptionResponse(HttpStatus httpStatus, String message, String orinService) {
        return ResponseEntity
                .status(httpStatus)
                .body(MessageDispatcherProblemDetailExceptionResponse.of(httpStatus.name(), httpStatus.value(), message, orinService));
    }

    protected record MessageDispatcherProblemDetailExceptionResponse(String type,
                                                                   String title,
                                                                   int status,
                                                                   String detail,
                                                                   String originService) {

        static MessageDispatcherProblemDetailExceptionResponse of(String title, int status, String detail, String originService) {
            return new MessageDispatcherProblemDetailExceptionResponse("Error", title, status, detail, originService);
        }
    }
}
