package br.com.ccs.messagedispatcher.exceptions.handler;

public record MessageDispatcherProblemDetailExceptionResponse(String type,
                                                              String title,
                                                              int status,
                                                              String detail,
                                                              String originService) {

    public static MessageDispatcherProblemDetailExceptionResponse of(String title, int status, String detail, String originService) {
        return new MessageDispatcherProblemDetailExceptionResponse("Error", title, status, detail, originService);
    }
}

