package edu.java.bot.service.client.scrapperClient.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public record ListLinkResponse(@JsonProperty List<LinkResponse> links,
                               @JsonProperty Integer size) {
}
