package edu.java.scrapper.domain.dao.jdbc;

import edu.java.scrapper.IntegrationTest;
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

@SpringBootTest
class JdbcTgChatTest extends IntegrationTest {

    @Autowired
    JdbcClient jdbcClient;
    @Autowired
    JdbcTgChatDao tgChatDao;

    @Test
    @Rollback
    @Transactional
    @DisplayName("Добавление chat")
    void add_shouldAddChat() {
        Long chatID = 2L;

        boolean wasAdd = tgChatDao.add(chatID);
        Chat result = jdbcClient.sql("SELECT * FROM chat")
            .query(Chat.class).single();

        assertThat(wasAdd)
            .isTrue();
        assertThat(result.id())
            .isEqualTo(chatID);
        assertThat(result.createdAt())
            .isCloseTo(OffsetDateTime.now(), within(5, ChronoUnit.SECONDS));
    }

    @Test
    @Rollback
    @Transactional
    @DisplayName("Повторное добавление chat")
    void add_shouldNotAddRepeatedChat() {
        Long chatID = 2L;
        tgChatDao.add(chatID);
        Chat expected = jdbcClient.sql("SELECT * FROM chat")
            .query(Chat.class).single();

        boolean wasAdd = tgChatDao.add(chatID);
        Chat result = jdbcClient.sql("SELECT * FROM chat")
            .query(Chat.class).single();

        assertThat(wasAdd)
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
            tgChatDao.add(id);
        }

        for (Long id : ids) {
            Chat result = tgChatDao.findById(id);
            assertThat(result)
                .isNotNull();
            assertThat(result.id())
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
            tgChatDao.add(id);
        }

        Chat result = tgChatDao.findById(0L);
        assertThat(result)
            .isNull();
    }

    @Test
    @Rollback
    @Transactional
    @DisplayName("Удаление chat")
    void remove_shouldRemoveChat() {
        Long chatID = 2L;
        tgChatDao.add(chatID);

        boolean wasRemoved = tgChatDao.remove(chatID);
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
    void remove_shouldNotRemoveNotExistingChat() {
        Long chatID = 2L;
        tgChatDao.add(chatID);

        boolean wasRemoved = tgChatDao.remove(1L);

        assertThat(wasRemoved)
            .isFalse();
    }

    @Test
    @Rollback
    @Transactional
    @DisplayName("Вывод всех chatID")
    void findAllIds_shouldReturnAllIds() {
        List<Long> ids = List.of(1L, 2L, 3L);
        for (Long id : ids) {
            tgChatDao.add(id);
        }

        List<Long> result = tgChatDao.findAllIds();

        assertThat(result)
            .isNotNull()
            .isEqualTo(ids);
    }

    @Test
    @Rollback
    @Transactional
    @DisplayName("Пустой вывод chatID")
    void findAllIds_shouldReturnEmptyListIfDBHasNoChats() {
        List<Long> result = tgChatDao.findAllIds();

        assertThat(result)
            .isNotNull()
            .isEmpty();
    }
}
