package edu.java.scrapper.domain.dao.jdbc;

import edu.java.scrapper.domain.dao.TgChatDao;
import edu.java.scrapper.domain.dto.Chat;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
public class JdbcTgChatDao implements TgChatDao {

    private static final String ADD_BY_ID_QUERY =
        "INSERT INTO chat (id) VALUES (?) ON CONFLICT DO NOTHING";
    private static final String DELETE_CHAT_BY_ID_QUERY =
        "DELETE FROM chat WHERE id = ?";
    private static final String DELETE_CHAT_LINKS_BY_ID_QUERY =
        "DELETE FROM chat_link WHERE chat_id = ?";
    private static final String SELECT_QUERY =
        "SELECT * FROM chat";
    private static final String SELECT_BY_ID_QUERY =
        "SELECT * FROM chat WHERE id = ?";

    private final JdbcClient jdbcClient;

    @Override
    public boolean add(Long chatID) {
        return jdbcClient.sql(ADD_BY_ID_QUERY)
            .param(chatID)
            .update() == 1;
    }

    @Override
    @Transactional
    public boolean remove(Long chatID) {
        jdbcClient.sql(DELETE_CHAT_LINKS_BY_ID_QUERY)
            .param(chatID)
            .update();
        return jdbcClient.sql(DELETE_CHAT_BY_ID_QUERY)
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
