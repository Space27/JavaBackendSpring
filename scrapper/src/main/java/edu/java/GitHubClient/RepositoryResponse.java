package edu.java.GitHubClient;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.net.URI;
import java.time.OffsetDateTime;

public record RepositoryResponse(@JsonProperty("name") String name,
                                 @JsonProperty("description") String description,
                                 @JsonProperty("html_url") URI uri,
                                 @JsonProperty("updated_at") OffsetDateTime update) {
}
