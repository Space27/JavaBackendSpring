package edu.java.scrapper.service.api.schemas;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.net.URI;

public record LinkResponse(@JsonProperty Long id,
                           @JsonProperty URI url) {
}
