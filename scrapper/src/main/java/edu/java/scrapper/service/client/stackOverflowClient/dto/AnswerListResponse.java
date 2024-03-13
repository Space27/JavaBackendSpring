package edu.java.scrapper.service.client.stackOverflowClient.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.OffsetDateTime;
import java.util.List;

public record AnswerListResponse(List<AnswerResponse> items) {
    public record AnswerResponse(Integer score,
                                 @JsonProperty("creation_date") OffsetDateTime createdAt) {

    }
}
