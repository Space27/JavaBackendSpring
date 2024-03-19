package edu.java.scrapper.domain.dao.jpa;

import edu.java.scrapper.IntegrationTest;
import edu.java.scrapper.domain.dao.jpa.entity.ChatEntity;
import edu.java.scrapper.domain.dto.Chat;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

@SpringBootTest(properties = { "app.database-access-type=jpa" })
public class JpaTgChatDaoTest extends IntegrationTest {

    @Autowired
    JdbcClient jdbcClient;
    @Autowired
    JpaTgChatDao tgChatDao;

    @Test
    @Rollback
    @Transactional
    @DisplayName("Добавление chat")
    void save_shouldAddChat() {
        Long chatID = 2L;
        ChatEntity chat = new ChatEntity();
        chat.setId(chatID);

        tgChatDao.saveAndFlush(chat);
        Chat result = jdbcClient.sql("SELECT * FROM chat")
            .query(Chat.class).single();

        assertThat(result.id())
            .isEqualTo(chatID);
        assertThat(result.createdAt())
            .isCloseTo(OffsetDateTime.now(), within(5, ChronoUnit.SECONDS));
    }

    @Test
    @Rollback
    @Transactional
    @DisplayName("Повторное добавление chat")
    void save_shouldNotAddRepeatedChat() {
        Long chatID = 2L;
        ChatEntity chat = new ChatEntity();
        chat.setId(chatID);
        tgChatDao.saveAndFlush(chat);
        Chat expected = jdbcClient.sql("SELECT * FROM chat")
            .query(Chat.class).single();

        chat = new ChatEntity();
        chat.setId(chatID);
        tgChatDao.saveAndFlush(chat);
        boolean notExists = tgChatDao.findById(chatID).isEmpty();
        Chat result = jdbcClient.sql("SELECT * FROM chat")
            .query(Chat.class).single();

        assertThat(notExists)
            .isFalse();
        assertThat(result)
            .isEqualTo(expected);
    }

    @Test
    @Rollback
    @Transactional
    @DisplayName("Поиск chat")
    void findById_shouldFindChat() {
        List<Long> ids = List.of(1L, 2L, 3L);
        for (Long id : ids) {
            ChatEntity chat = new ChatEntity();
            chat.setId(id);
            tgChatDao.saveAndFlush(chat);
        }

        for (Long id : ids) {
            ChatEntity result = tgChatDao.findById(id).orElse(null);
            assertThat(result)
                .isNotNull();
            assertThat(result.getId())
                .isEqualTo(id);
        }
    }

    @Test
    @Rollback
    @Transactional
    @DisplayName("Поиск несуществующего chat")
    void findById_shouldNotFindNotExistingChat() {
        List<Long> ids = List.of(1L, 2L, 3L);
        for (Long id : ids) {
            ChatEntity chat = new ChatEntity();
            chat.setId(id);
            tgChatDao.saveAndFlush(chat);
        }

        ChatEntity result = tgChatDao.findById(0L).orElse(null);
        assertThat(result)
            .isNull();
    }

    @Test
    @Rollback
    @Transactional
    @DisplayName("Удаление chat")
    void deleteById_shouldRemoveChat() {
        Long chatID = 2L;
        ChatEntity chat = new ChatEntity();
        chat.setId(chatID);
        tgChatDao.saveAndFlush(chat);

        tgChatDao.deleteById(chatID);
        boolean wasRemoved = !tgChatDao.existsById(chatID);
        Optional<Chat> result = jdbcClient.sql("SELECT * FROM chat")
            .query(Chat.class).optional();

        assertThat(wasRemoved)
            .isTrue();
        assertThat(result)
            .isEmpty();
    }

    @Test
    @Rollback
    @Transactional
    @DisplayName("Удаление несуществующего chat")
    void deleteById_shouldNotRemoveNotExistingChat() {
        Long chatID = 2L;
        ChatEntity chat = new ChatEntity();
        chat.setId(chatID);
        tgChatDao.saveAndFlush(chat);

        tgChatDao.deleteById(1L);
        Optional<Chat> result = jdbcClient.sql("SELECT * FROM chat")
            .query(Chat.class).optional();

        assertThat(result)
            .isNotEmpty();
    }

    @Test
    @Rollback
    @Transactional
    @DisplayName("Вывод всех chat")
    void findAll_shouldReturnAllIds() {
        List<Long> ids = List.of(1L, 2L, 3L);
        for (Long id : ids) {
            ChatEntity chat = new ChatEntity();
            chat.setId(id);
            tgChatDao.saveAndFlush(chat);
        }

        List<ChatEntity> result = tgChatDao.findAll();

        assertThat(result.stream().map(ChatEntity::getId).toList())
            .isNotNull()
            .hasSize(3);
    }

    @Test
    @Rollback
    @Transactional
    @DisplayName("Пустой вывод chat")
    void findAll_shouldReturnEmptyListIfDBHasNoChats() {
        List<ChatEntity> result = tgChatDao.findAll();

        assertThat(result)
            .isNotNull()
            .isEmpty();
    }
}
