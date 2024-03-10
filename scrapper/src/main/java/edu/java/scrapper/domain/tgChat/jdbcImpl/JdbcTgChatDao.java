package edu.java.scrapper.domain.tgChat.jdbcImpl;

import edu.java.scrapper.domain.tgChat.Chat;
import edu.java.scrapper.domain.tgChat.TgChatDao;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class JdbcTgChatDao implements TgChatDao {

    private static final String ADD_BY_ID_QUERY = "INSERT INTO chat (id) VALUES (?) ON CONFLICT DO NOTHING";
    private static final String DELETE_BY_ID_QUERY = "DELETE FROM chat WHERE id = ?";
    private static final String SELECT_QUERY = "SELECT * FROM chat";
    private static final String SELECT_BY_ID_QUERY = "SELECT * FROM chat WHERE id = ?";

    private final JdbcClient jdbcClient;

    @Override
    public boolean add(Long chatID) {
        return jdbcClient.sql(ADD_BY_ID_QUERY)
            .param(chatID)
            .update() == 1;
    }

    @Override
    public boolean remove(Long chatID) {
        return jdbcClient.sql(DELETE_BY_ID_QUERY)
            .param(chatID)
            .update() == 1;
    }

    @Override
    public Chat findById(Long chatID) {
        return jdbcClient.sql(SELECT_BY_ID_QUERY)
            .param(chatID)
            .query(Chat.class)
            .optional().orElse(null);
    }

    @Override
    public List<Chat> findAll() {
        return jdbcClient.sql(SELECT_QUERY)
            .query(Chat.class)
            .list();
    }

    public List<Long> findAllIds() {
        return findAll().stream()
            .map(Chat::id)
            .toList();
    }
}
