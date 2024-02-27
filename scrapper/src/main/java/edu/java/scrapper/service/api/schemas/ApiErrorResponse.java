package edu.java.scrapper.service.api.schemas;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public record ApiErrorResponse(@JsonProperty String description,
                               @JsonProperty String code,
                               @JsonProperty String exceptionName,
                               @JsonProperty String exceptionMessage,
                               @JsonProperty List<String> stacktrace) {
}
