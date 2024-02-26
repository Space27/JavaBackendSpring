package edu.java.bot.service.api.schemas;

import com.fasterxml.jackson.annotation.JsonProperty;
import edu.java.bot.service.api.validators.IsURI;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.net.URI;
import java.util.List;

public record LinkUpdate(@JsonProperty("id") @NotNull @Positive Long id,
                         @JsonProperty("url") @NotNull @IsURI URI url,
                         @JsonProperty("description") @NotNull @NotBlank String description,
                         @JsonProperty("tgChatIds") @NotNull @NotEmpty List<@Positive Long> tgChatIds) {
}
