package edu.java.scrapper.domain.TgChat.jdbcImpl;

import edu.java.scrapper.domain.TgChat.Chat;
import edu.java.scrapper.domain.TgChat.TgChatDao;
import java.time.OffsetDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class JdbcTgChatDao implements TgChatDao {

    private final JdbcClient jdbcClient;

    @Override
    public int add(Long chatID) {
        String sql = "INSERT INTO chat (id)"
            + "VALUES (?)";

        return jdbcClient.sql(sql)
            .param(chatID)
            .update();
    }

    @Override
    public int add(Long chatID, OffsetDateTime offsetDateTime) {
        String sql = "INSERT INTO chat (id, created_at)"
            + "VALUES (?, ?)";

        return jdbcClient.sql(sql)
            .param(chatID)
            .param(offsetDateTime)
            .update();
    }

    @Override
    public int add(Chat chat) {
        return add(chat.id(), chat.createdAt());
    }

    @Override
    public int remove(Long chatID) {
        String sql = "DELETE FROM chat "
            + "WHERE id = ?";

        return jdbcClient.sql(sql)
            .param(chatID)
            .update();
    }

    @Override
    public List<Chat> findAll() {
        String sql = "SELECT * FROM chat";

        return jdbcClient.sql(sql)
            .query(Chat.class).list();
    }
}
