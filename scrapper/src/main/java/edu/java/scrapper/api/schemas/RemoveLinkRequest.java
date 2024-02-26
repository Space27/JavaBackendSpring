package edu.java.scrapper.api.schemas;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import java.net.URI;

public record RemoveLinkRequest(@JsonProperty("link") @NotNull URI link) {
}
