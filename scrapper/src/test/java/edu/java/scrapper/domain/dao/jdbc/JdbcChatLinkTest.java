package edu.java.scrapper.domain.dao.jdbc;

import edu.java.scrapper.IntegrationTest;
import edu.java.scrapper.domain.dao.jdbc.JdbcChatLinkDao;
import edu.java.scrapper.domain.dto.ChatLink;
import edu.java.scrapper.domain.dto.Link;
import edu.java.scrapper.domain.dao.jdbc.JdbcLinkDao;
import edu.java.scrapper.domain.dto.Chat;
import edu.java.scrapper.domain.dao.jdbc.JdbcTgChatDao;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import java.net.URI;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

@SpringBootTest
class JdbcChatLinkTest extends IntegrationTest {

    @Autowired
    JdbcClient jdbcClient;
    @Autowired
    JdbcChatLinkDao chatLinkDao;
    @Autowired
    JdbcTgChatDao chatDao;
    @Autowired
    JdbcLinkDao linkDao;

    @Test
    @Transactional
    @Rollback
    @DisplayName("Добавление связи")
    void add_shouldAddRelation() {
        Long chatID = 1L;
        URI url = URI.create("https://edu.tinkoff.ru/");
        chatDao.add(chatID);
        Link link = linkDao.add(url);

        boolean wasAdd = chatLinkDao.add(chatID, link.id());
        ChatLink result = jdbcClient.sql("SELECT * FROM chat_link")
            .query(ChatLink.class).single();

        assertThat(wasAdd)
            .isTrue();
        assertThat(result.chatId())
            .isEqualTo(chatID);
        assertThat(result.linkId())
            .isEqualTo(link.id());
        assertThat(result.createdAt())
            .isCloseTo(OffsetDateTime.now(), within(5, ChronoUnit.SECONDS));
    }

    @Test
    @Transactional
    @Rollback
    @DisplayName("Повторное добавление связи")
    void add_shouldNotAddRepeatRelation() {
        Long chatID = 1L;
        URI url = URI.create("https://edu.tinkoff.ru/");
        chatDao.add(chatID);
        Link link = linkDao.add(url);
        chatLinkDao.add(chatID, link.id());

        boolean wasAdd = chatLinkDao.add(chatID, link.id());

        assertThat(wasAdd)
            .isFalse();
    }

    @Test
    @Transactional
    @Rollback
    @DisplayName("Некорректное добавление связи")
    void add_shouldNotAddIncorrectRelation() {
        Long chatID = 1L;
        chatDao.add(chatID);

        boolean wasAdd = chatLinkDao.add(chatID, 0L);

        assertThat(wasAdd)
            .isFalse();
    }

    @Test
    @Transactional
    @Rollback
    @DisplayName("Удаление связи")
    void remove_shouldRemoveRelation() {
        Long chatID = 1L;
        URI url = URI.create("https://edu.tinkoff.ru/");
        chatDao.add(chatID);
        Link link = linkDao.add(url);
        chatLinkDao.add(chatID, link.id());

        boolean wasRemoved = chatLinkDao.remove(chatID, link.id());
        ChatLink result = jdbcClient.sql("SELECT * FROM chat_link")
            .query(ChatLink.class).optional().orElse(null);

        assertThat(wasRemoved)
            .isTrue();
        assertThat(result)
            .isNull();
    }

    @Test
    @Transactional
    @Rollback
    @DisplayName("Удаление несуществующей связи")
    void remove_shouldNotRemoveNotExistingRelation() {
        boolean wasRemoved = chatLinkDao.remove(0L, 0L);

        assertThat(wasRemoved)
            .isFalse();
    }

    @Test
    @Transactional
    @Rollback
    @DisplayName("Каскадное удаление связи")
    void onRemove_shouldRemoveRelation() {
        Long chatID = 1L;
        URI url = URI.create("https://edu.tinkoff.ru/");
        chatDao.add(chatID);
        Link link = linkDao.add(url);
        chatLinkDao.add(chatID, link.id());

        chatDao.remove(chatID);
        ChatLink result = jdbcClient.sql("SELECT * FROM chat_link")
            .query(ChatLink.class).optional().orElse(null);

        assertThat(result)
            .isNull();
    }

    @Test
    @Transactional
    @Rollback
    @DisplayName("Список связей")
    void findAll_shouldListAllRelations() {
        List<Long> chatIDs = List.of(1L, 2L);
        List<URI> urls = List.of(
            URI.create("https://edu.tinkoff.ru/"),
            URI.create("https://github.com/"),
            URI.create("https://lk.etu.ru/")
        );
        List<Link> links = new ArrayList<>();
        for (Long id : chatIDs) {
            chatDao.add(id);
        }
        for (URI url : urls) {
            links.add(linkDao.add(url));
        }
        chatLinkDao.add(1L, links.get(0).id());
        chatLinkDao.add(1L, links.get(2).id());
        chatLinkDao.add(2L, links.get(1).id());
        chatLinkDao.add(2L, links.get(2).id());

        List<ChatLink> chatLinks = chatLinkDao.findAll();

        assertThat(chatLinks)
            .isNotNull()
            .hasSize(4)
            .doesNotHaveDuplicates();
    }

    @Test
    @Transactional
    @Rollback
    @DisplayName("Список связей по чату")
    void findAllByChat_shouldListAllRelations() {
        List<Long> chatIDs = List.of(1L, 2L);
        List<URI> urls = List.of(
            URI.create("https://edu.tinkoff.ru/"),
            URI.create("https://github.com/"),
            URI.create("https://lk.etu.ru/")
        );
        List<Link> links = new ArrayList<>();
        for (Long id : chatIDs) {
            chatDao.add(id);
        }
        for (URI url : urls) {
            links.add(linkDao.add(url));
        }
        chatLinkDao.add(1L, links.get(0).id());
        chatLinkDao.add(1L, links.get(2).id());
        chatLinkDao.add(2L, links.get(1).id());
        chatLinkDao.add(2L, links.get(2).id());

        List<ChatLink> chatLinks = chatLinkDao.findAllByChat(1L);

        assertThat(chatLinks)
            .isNotNull()
            .hasSize(2)
            .doesNotHaveDuplicates();
    }

    @Test
    @Transactional
    @Rollback
    @DisplayName("Список связей по ссылке")
    void findAllByLink_shouldListAllRelations() {
        List<Long> chatIDs = List.of(1L, 2L);
        List<URI> urls = List.of(
            URI.create("https://edu.tinkoff.ru/"),
            URI.create("https://github.com/"),
            URI.create("https://lk.etu.ru/")
        );
        List<Link> links = new ArrayList<>();
        for (Long id : chatIDs) {
            chatDao.add(id);
        }
        for (URI url : urls) {
            links.add(linkDao.add(url));
        }
        chatLinkDao.add(1L, links.get(0).id());
        chatLinkDao.add(1L, links.get(2).id());
        chatLinkDao.add(2L, links.get(1).id());
        chatLinkDao.add(2L, links.get(2).id());

        List<ChatLink> chatLinks = chatLinkDao.findAllByLink(links.get(2).id());

        assertThat(chatLinks)
            .isNotNull()
            .hasSize(2)
            .doesNotHaveDuplicates();
    }

    @Test
    @Transactional
    @Rollback
    @DisplayName("Список ссылок по чату")
    void findAllByChat_shouldListAllLinks() {
        List<Long> chatIDs = List.of(1L, 2L);
        List<URI> urls = List.of(
            URI.create("https://edu.tinkoff.ru/"),
            URI.create("https://github.com/"),
            URI.create("https://lk.etu.ru/")
        );
        List<Link> links = new ArrayList<>();
        for (Long id : chatIDs) {
            chatDao.add(id);
        }
        for (URI url : urls) {
            links.add(linkDao.add(url));
        }
        chatLinkDao.add(1L, links.get(0).id());
        chatLinkDao.add(1L, links.get(2).id());
        chatLinkDao.add(2L, links.get(1).id());
        chatLinkDao.add(2L, links.get(2).id());

        List<Link> answer = chatLinkDao.findLinksByChat(1L);

        assertThat(answer)
            .isNotNull()
            .hasSize(2)
            .doesNotHaveDuplicates()
            .contains(links.get(0), links.get(2));
    }

    @Test
    @Transactional
    @Rollback
    @DisplayName("Список чатов по ссылке")
    void findAllByUrl_shouldListAllChats() {
        List<Long> chatIDs = List.of(1L, 2L);
        List<URI> urls = List.of(
            URI.create("https://edu.tinkoff.ru/"),
            URI.create("https://github.com/"),
            URI.create("https://lk.etu.ru/")
        );
        List<Link> links = new ArrayList<>();
        for (Long id : chatIDs) {
            chatDao.add(id);
        }
        for (URI url : urls) {
            links.add(linkDao.add(url));
        }
        chatLinkDao.add(1L, links.get(0).id());
        chatLinkDao.add(1L, links.get(2).id());
        chatLinkDao.add(2L, links.get(1).id());
        chatLinkDao.add(2L, links.get(2).id());

        List<Chat> answer = chatLinkDao.findChatsByLink(links.get(2).id());

        assertThat(answer)
            .isNotNull()
            .hasSize(2)
            .doesNotHaveDuplicates();
    }
}
