package edu.java.scrapper.service.client.gitHub.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.net.URI;
import java.time.OffsetDateTime;

public record RepositoryResponse(String name,
                                 String description,
                                 @JsonProperty("html_url") URI uri,
                                 @JsonProperty("updated_at") OffsetDateTime update) {
}
