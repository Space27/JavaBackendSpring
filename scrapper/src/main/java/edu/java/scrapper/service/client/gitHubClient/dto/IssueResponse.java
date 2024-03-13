package edu.java.scrapper.service.client.gitHubClient.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.OffsetDateTime;

public record IssueResponse(String title,
                            @JsonProperty("created_at") OffsetDateTime createdAt) {
}
