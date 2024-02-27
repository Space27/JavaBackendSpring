package edu.java.bot.service.clients.ScrapperClient;

import edu.java.bot.service.api.schemas.ApiErrorResponse;
import lombok.Getter;

@Getter
public class ResponseErrorException extends RuntimeException {

    private final ApiErrorResponse apiErrorResponse;

    public ResponseErrorException(ApiErrorResponse apiErrorResponse) {
        this.apiErrorResponse = apiErrorResponse;
    }
}
