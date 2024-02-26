package edu.java.scrapper.api.schemas;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public record ListLinkResponse(@JsonProperty("links") List<LinkResponse> links,
                               @JsonProperty("size") Integer size) {
}
