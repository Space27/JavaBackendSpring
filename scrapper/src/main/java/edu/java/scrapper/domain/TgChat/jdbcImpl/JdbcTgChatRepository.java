package edu.java.scrapper.domain.TgChat.jdbcImpl;

import edu.java.scrapper.domain.TgChat.Chat;
import edu.java.scrapper.domain.TgChat.TgChatDao;
import edu.java.scrapper.domain.TgChat.TgChatRepository;
import java.time.OffsetDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class JdbcTgChatRepository implements TgChatRepository {

    private final TgChatDao tgChatDao;

    @Override
    public boolean add(Long chatID) {
        return tgChatDao.add(chatID) == 1;
    }

    @Override
    public boolean add(Long chatID, OffsetDateTime offsetDateTime) {
        return tgChatDao.add(chatID, offsetDateTime) == 1;
    }

    @Override
    public boolean add(Chat chat) {
        return tgChatDao.add(chat) == 1;
    }

    @Override
    public boolean remove(Long chatID) {
        return tgChatDao.remove(chatID) == 1;
    }

    @Override
    public List<Chat> findAll() {
        return tgChatDao.findAll();
    }

    @Override
    public List<Long> findAllIds() {
        return findAll().stream()
            .map(Chat::id)
            .toList();
    }
}
