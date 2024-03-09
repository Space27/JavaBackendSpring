package edu.java.scrapper.domain.TgChat;

import java.time.OffsetDateTime;
import java.util.List;

public interface TgChatRepository {

    boolean add(Long chatID);

    boolean add(Long chatID, OffsetDateTime offsetDateTime);

    boolean add(Chat chat);

    boolean remove(Long chatID);

    List<Chat> findAll();

    List<Long> findAllIds();
}
