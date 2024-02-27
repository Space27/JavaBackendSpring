package edu.java.scrapper.service.clients.BotClient.schemas;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.net.URI;
import java.util.List;

public record LinkUpdate(@JsonProperty Long id,
                         @JsonProperty URI url,
                         @JsonProperty String description,
                         @JsonProperty List<Long> tgChatIds) {
}
