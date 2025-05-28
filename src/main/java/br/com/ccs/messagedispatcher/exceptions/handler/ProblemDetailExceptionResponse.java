package br.com.ccs.messagedispatcher.exceptions.handler;

public record ProblemDetailExceptionResponse(String type,
                                             String title,
                                             int status,
                                             String detail) {

    public static ProblemDetailExceptionResponse of(String title, int status, String detail) {
        return new ProblemDetailExceptionResponse("Error", title, status, detail);
    }
}

