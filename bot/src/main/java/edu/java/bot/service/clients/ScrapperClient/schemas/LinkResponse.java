package edu.java.bot.service.clients.ScrapperClient.schemas;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.net.URI;

public record LinkResponse(@JsonProperty Long id,
                           @JsonProperty URI url) {
}
