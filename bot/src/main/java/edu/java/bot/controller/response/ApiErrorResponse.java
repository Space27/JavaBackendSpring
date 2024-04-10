package edu.java.bot.controller.response;

import java.util.List;

public record ApiErrorResponse(String description,
                               String code,
                               String exceptionName,
                               String exceptionMessage,
                               List<String> stacktrace) {

    public ApiErrorResponse() {
        this(null, null, null, null, null);
    }
}
