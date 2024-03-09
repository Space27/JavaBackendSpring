package edu.java.scrapper.domain.TgChat;

import java.time.OffsetDateTime;
import java.util.List;

public interface TgChatDao {

    int add(Long chatID);

    int add(Long chatID, OffsetDateTime offsetDateTime);

    int add(Chat chat);

    int remove(Long chatID);

    List<Chat> findAll();
}
