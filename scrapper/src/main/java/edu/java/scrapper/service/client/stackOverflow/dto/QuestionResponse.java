package edu.java.scrapper.service.client.stackOverflow.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.net.URI;
import java.time.OffsetDateTime;
import java.util.List;

public record QuestionResponse(List<ItemResponse> items) {
    public record ItemResponse(String title,
                               @JsonProperty("link") URI uri,
                               @JsonProperty("answer_count") int answerCount,
                               @JsonProperty("last_activity_date") OffsetDateTime update) {
    }
}
