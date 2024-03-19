package edu.java.scrapper.controller.request;

import edu.java.scrapper.controller.validator.SupportedLink;
import jakarta.validation.constraints.NotNull;
import java.net.URI;

public record AddLinkRequest(@NotNull @SupportedLink URI link) {
}
