package edu.java.scrapper.domain.dto;

import java.net.URI;
import java.time.OffsetDateTime;

public record Link(Long id, URI url, OffsetDateTime lastCheckAt, OffsetDateTime createdAt) {
}
