package br.com.messagedispatcher.exceptions;

import org.springframework.amqp.ImmediateRequeueAmqpException;

public class MessageDispatcherRetryableException extends ImmediateRequeueAmqpException {
    public MessageDispatcherRetryableException(String message, Throwable cause) {
        super(message, cause);
    }
}
