package edu.java.scrapper.service.clients.BotClient;

import edu.java.scrapper.service.api.schemas.ApiErrorResponse;
import lombok.Getter;

@Getter
public class ResponseErrorException extends RuntimeException {

    private final ApiErrorResponse apiErrorResponse;

    public ResponseErrorException(ApiErrorResponse apiErrorResponse) {
        this.apiErrorResponse = apiErrorResponse;
    }
}
