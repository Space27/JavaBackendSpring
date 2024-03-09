package edu.java.bot.controller.request;

import edu.java.bot.controller.validator.IsURI;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.net.URI;
import java.util.List;

public record LinkUpdateRequest(@NotNull @Positive Long id,
                                @NotNull @IsURI URI url,
                                @NotNull @NotBlank String description,
                                @NotNull @NotEmpty List<@Positive Long> tgChatIds) {
}
