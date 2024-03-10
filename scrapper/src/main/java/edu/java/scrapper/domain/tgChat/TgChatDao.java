package edu.java.scrapper.domain.tgChat;

import java.util.List;

public interface TgChatDao {

    boolean add(Long chatID);

    boolean remove(Long chatID);

    Chat findById(Long chatID);

    List<Chat> findAll();

    List<Long> findAllIds();
}
