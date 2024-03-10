package edu.java.scrapper.domain.link;

import java.net.URI;
import java.time.OffsetDateTime;

public record Link(Long id, URI url, OffsetDateTime lastCheckAt, OffsetDateTime createdAt) {
}
