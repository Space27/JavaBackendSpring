package edu.java.bot.service.scrapperClient.schemas;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public record ListLinkResponse(@JsonProperty("links") @NotNull List<LinkResponse> links,
                               @JsonProperty("size") @NotNull Integer size) {
}
