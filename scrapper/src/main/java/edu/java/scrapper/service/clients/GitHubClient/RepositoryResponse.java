package edu.java.scrapper.service.clients.GitHubClient;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.net.URI;
import java.time.OffsetDateTime;

public record RepositoryResponse(@JsonProperty String name,
                                 @JsonProperty String description,
                                 @JsonProperty("html_url") URI uri,
                                 @JsonProperty("updated_at") OffsetDateTime update) {
}
