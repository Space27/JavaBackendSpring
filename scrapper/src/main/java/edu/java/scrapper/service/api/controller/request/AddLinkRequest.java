package edu.java.scrapper.service.api.controller.request;

import jakarta.validation.constraints.NotNull;
import java.net.URI;

public record AddLinkRequest(@NotNull URI link) {
}
