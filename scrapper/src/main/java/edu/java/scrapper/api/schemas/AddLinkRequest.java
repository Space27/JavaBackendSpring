package edu.java.scrapper.api.schemas;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import java.net.URI;

public record AddLinkRequest(@JsonProperty("link") @NotNull URI link) {
}
