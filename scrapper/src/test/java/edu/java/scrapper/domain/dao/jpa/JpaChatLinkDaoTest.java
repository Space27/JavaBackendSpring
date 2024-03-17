package edu.java.scrapper.domain.dao.jpa;

import edu.java.scrapper.IntegrationTest;
import edu.java.scrapper.domain.dao.jpa.entity.ChatEntity;
import edu.java.scrapper.domain.dao.jpa.entity.ChatLinkEntity;
import edu.java.scrapper.domain.dao.jpa.entity.LinkEntity;
import edu.java.scrapper.domain.dto.ChatLink;
import java.net.URI;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
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
public class JpaChatLinkDaoTest extends IntegrationTest {

    @Autowired
    JdbcClient jdbcClient;
    @Autowired
    JpaLinkDao linkDao;
    @Autowired
    JpaTgChatDao chatDao;
    @Autowired
    JpaChatLinkDao chatLinkDao;

    @Test
    @Transactional
    @Rollback
    @DisplayName("Добавление связи")
    void save_shouldAddRelation() {
        Long chatID = 1L;
        URI url = URI.create("https://edu.tinkoff.ru/");
        ChatEntity chat = new ChatEntity();
        LinkEntity link = new LinkEntity();
        chat.setId(chatID);
        link.setUrl(url.toString());
        chatDao.saveAndFlush(chat);
        linkDao.saveAndFlush(link);
        ChatLinkEntity chatLink = new ChatLinkEntity();
        chatLink.setChat(chat);
        chatLink.setLink(link);

        chatLinkDao.saveAndFlush(chatLink);
        ChatLink result = jdbcClient.sql("SELECT * FROM chat_link")
            .query(ChatLink.class).single();

        assertThat(result.chatId())
            .isEqualTo(chatID);
        assertThat(result.linkId())
            .isEqualTo(link.getId());
        assertThat(result.createdAt())
            .isCloseTo(OffsetDateTime.now(), within(5, ChronoUnit.SECONDS));
        assertThat(chatLink.getLink())
            .isEqualTo(link);
        assertThat(chatLink.getChat())
            .isEqualTo(chat);
    }

    @Test
    @Transactional
    @Rollback
    @DisplayName("Удаление связи")
    void delete_shouldRemoveRelation() {
        Long chatID = 1L;
        URI url = URI.create("https://edu.tinkoff.ru/");
        ChatEntity chat = new ChatEntity();
        LinkEntity link = new LinkEntity();
        chat.setId(chatID);
        link.setUrl(url.toString());
        chatDao.saveAndFlush(chat);
        linkDao.saveAndFlush(link);
        ChatLinkEntity chatLink = new ChatLinkEntity();
        chatLink.setChat(chat);
        chatLink.setLink(link);
        chatLinkDao.saveAndFlush(chatLink);

        chatLinkDao.deleteById(chatLink.getId());
        chatLinkDao.flush();
        ChatLink result = jdbcClient.sql("SELECT * FROM chat_link")
            .query(ChatLink.class).optional().orElse(null);

        assertThat(result)
            .isNull();
        assertThat(chatDao.findById(chatID))
            .isNotEmpty();
    }

    @Test
    @Transactional
    @Rollback
    @DisplayName("Список связей")
    void findAll_shouldListAllRelations() {
        List<ChatEntity> chats = List.of(new ChatEntity(1L), new ChatEntity(2L));
        List<LinkEntity> links = List.of(
            new LinkEntity("https://edu.tinkoff.ru/"),
            new LinkEntity("https://github.com/"),
            new LinkEntity("https://lk.etu.ru/")
        );
        List<ChatLinkEntity> chatLinkEntities = List.of(
            new ChatLinkEntity(chats.getFirst(), links.get(0)),
            new ChatLinkEntity(chats.getFirst(), links.get(2)),
            new ChatLinkEntity(chats.getLast(), links.get(1)),
            new ChatLinkEntity(chats.getLast(), links.get(2))
        );
        linkDao.saveAllAndFlush(links);
        chatDao.saveAllAndFlush(chats);
        chatLinkDao.saveAllAndFlush(chatLinkEntities);

        List<ChatLinkEntity> result = chatLinkDao.findAll();

        assertThat(result)
            .isNotNull()
            .hasSize(4)
            .doesNotHaveDuplicates()
            .isEqualTo(chatLinkEntities);
    }

    @Test
    @Transactional
    @Rollback
    @DisplayName("Список связей по чату")
    void findAllByChat_shouldListAllRelations() {
        List<ChatEntity> chats = List.of(new ChatEntity(1L), new ChatEntity(2L));
        List<LinkEntity> links = List.of(
            new LinkEntity("https://edu.tinkoff.ru/"),
            new LinkEntity("https://github.com/"),
            new LinkEntity("https://lk.etu.ru/")
        );
        List<ChatLinkEntity> chatLinkEntities = List.of(
            new ChatLinkEntity(chats.getFirst(), links.get(0)),
            new ChatLinkEntity(chats.getFirst(), links.get(2)),
            new ChatLinkEntity(chats.getLast(), links.get(1)),
            new ChatLinkEntity(chats.getLast(), links.get(2))
        );
        linkDao.saveAllAndFlush(links);
        chatDao.saveAllAndFlush(chats);
        chatLinkDao.saveAllAndFlush(chatLinkEntities);

        List<ChatLinkEntity> result = chatLinkDao.findAllByChat(chats.getFirst());

        assertThat(result)
            .isNotNull()
            .hasSize(2)
            .doesNotHaveDuplicates();
        assertThat(result.stream().map(ChatLinkEntity::getChat).map(ChatEntity::getId).toList())
            .containsOnly(1L);
    }

    @Test
    @Transactional
    @Rollback
    @DisplayName("Список связей по ссылке")
    void findAllByLink_shouldListAllRelations() {
        List<ChatEntity> chats = List.of(new ChatEntity(1L), new ChatEntity(2L));
        List<LinkEntity> links = List.of(
            new LinkEntity("https://edu.tinkoff.ru/"),
            new LinkEntity("https://github.com/"),
            new LinkEntity("https://lk.etu.ru/")
        );
        List<ChatLinkEntity> chatLinkEntities = List.of(
            new ChatLinkEntity(chats.getFirst(), links.get(0)),
            new ChatLinkEntity(chats.getFirst(), links.get(2)),
            new ChatLinkEntity(chats.getLast(), links.get(1)),
            new ChatLinkEntity(chats.getLast(), links.get(2))
        );
        linkDao.saveAllAndFlush(links);
        chatDao.saveAllAndFlush(chats);
        chatLinkDao.saveAllAndFlush(chatLinkEntities);

        List<ChatLinkEntity> result = chatLinkDao.findAllByLink(links.get(2));

        assertThat(result)
            .isNotNull()
            .hasSize(2)
            .doesNotHaveDuplicates();
    }
}
