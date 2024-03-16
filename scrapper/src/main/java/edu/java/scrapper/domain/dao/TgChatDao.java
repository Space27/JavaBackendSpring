package edu.java.scrapper.domain.dao;

import edu.java.scrapper.domain.dto.Chat;
import java.util.List;

public interface TgChatDao {

    boolean add(Long chatID);

    boolean remove(Long chatID);

    Chat findById(Long chatID);

    List<Chat> findAll();

    List<Long> findAllIds();
}
