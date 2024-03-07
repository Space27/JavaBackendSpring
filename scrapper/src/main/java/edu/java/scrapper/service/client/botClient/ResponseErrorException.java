package edu.java.scrapper.service.client.botClient;

import edu.java.scrapper.controller.response.ApiErrorResponse;
import lombok.Getter;

@Getter
public class ResponseErrorException extends RuntimeException {

    private final ApiErrorResponse apiErrorResponse;

    public ResponseErrorException(ApiErrorResponse apiErrorResponse) {
        this.apiErrorResponse = apiErrorResponse;
    }
}
