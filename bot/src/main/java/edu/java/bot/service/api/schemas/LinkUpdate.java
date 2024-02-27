package edu.java.bot.service.api.schemas;

import com.fasterxml.jackson.annotation.JsonProperty;
import edu.java.bot.service.api.validators.IsURI;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.net.URI;
import java.util.List;

public record LinkUpdate(@JsonProperty @NotNull @Positive Long id,
                         @JsonProperty @NotNull @IsURI URI url,
                         @JsonProperty @NotNull @NotBlank String description,
                         @JsonProperty @NotNull @NotEmpty List<@Positive Long> tgChatIds) {
}
