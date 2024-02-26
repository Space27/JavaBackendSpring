package edu.java.bot.service.scrapperClient.schemas;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.net.URI;

public record LinkResponse(@JsonProperty("id") @NotNull @Positive Long id,
                           @JsonProperty("url") @NotNull URI url) {
}
