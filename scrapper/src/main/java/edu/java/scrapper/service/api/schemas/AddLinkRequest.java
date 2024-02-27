package edu.java.scrapper.service.api.schemas;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import java.net.URI;

public record AddLinkRequest(@JsonProperty @NotNull URI link) {
}
