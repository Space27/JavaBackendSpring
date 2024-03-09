package edu.java.scrapper.domain;

import edu.java.scrapper.IntegrationTest;
import edu.java.scrapper.domain.TgChat.Chat;
import edu.java.scrapper.domain.TgChat.jdbcImpl.JdbcTgChatRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import java.time.OffsetDateTime;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class JdbcTgChatTest extends IntegrationTest {

    @Autowired
    JdbcClient jdbcClient;
    @Autowired
    JdbcTgChatRepository tgChatRepository;

    @Test
    @DisplayName("Добавление цельного chat")
    void add_shouldAddChat() {
        Chat chat = new Chat(2L, OffsetDateTime.now().minusDays(1));

        boolean wasAdd = tgChatRepository.add(chat);
        Chat result = jdbcClient.sql("SELECT * FROM chat")
            .query(Chat.class).single();

        assertThat(wasAdd)
            .isTrue();
        assertThat(result)
            .isEqualTo(chat);
    }
}
