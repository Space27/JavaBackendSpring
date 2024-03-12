package edu.java.scrapper.domain.dto;

import java.time.OffsetDateTime;

public record ChatLink(Long chatId, Long linkId, OffsetDateTime createdAt) {
}
