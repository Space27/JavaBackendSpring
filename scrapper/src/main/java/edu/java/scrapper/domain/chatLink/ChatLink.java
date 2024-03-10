package edu.java.scrapper.domain.chatLink;

import java.time.OffsetDateTime;

public record ChatLink(Long chatId, Long linkId, OffsetDateTime createdAt) {
}
