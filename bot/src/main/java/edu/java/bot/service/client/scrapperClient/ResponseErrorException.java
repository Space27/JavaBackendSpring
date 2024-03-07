package edu.java.bot.service.client.scrapperClient;

import edu.java.bot.controller.response.ApiErrorResponse;
import lombok.Getter;

@Getter
public class ResponseErrorException extends RuntimeException {

    private final ApiErrorResponse apiErrorResponse;

    public ResponseErrorException(ApiErrorResponse apiErrorResponse) {
        this.apiErrorResponse = apiErrorResponse;
    }
}
