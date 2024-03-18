package edu.java.scrapper.service.daoService.jpa;

import edu.java.scrapper.controller.linksApi.exception.ChatNotExistsException;
import edu.java.scrapper.controller.linksApi.exception.LinkAlreadyExistsException;
import edu.java.scrapper.controller.linksApi.exception.LinkNotFoundException;
import edu.java.scrapper.domain.dao.jpa.JpaChatLinkDao;
import edu.java.scrapper.domain.dao.jpa.JpaLinkDao;
import edu.java.scrapper.domain.dao.jpa.JpaTgChatDao;
import edu.java.scrapper.domain.dao.jpa.entity.ChatEntity;
import edu.java.scrapper.domain.dao.jpa.entity.ChatLinkEntity;
import edu.java.scrapper.domain.dao.jpa.entity.LinkEntity;
import edu.java.scrapper.domain.dto.Chat;
import edu.java.scrapper.domain.dto.Link;
import java.net.URI;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;

public class JpaLinkServiceTest {

    JpaLinkService linkService;
    JpaTgChatDao chatDao;
    JpaLinkDao linkDao;
    JpaChatLinkDao chatLinkDao;

    @BeforeEach
    void init() {
        chatDao = Mockito.mock(JpaTgChatDao.class);
        linkDao = Mockito.mock(JpaLinkDao.class);
        chatLinkDao = Mockito.mock(JpaChatLinkDao.class);

        linkService = new JpaLinkService(chatLinkDao, linkDao, chatDao);
    }

    @Test
    @DisplayName("Чат не существует")
    void calls_shouldThrowExceptionIfChatNotExists() {
        URI url = URI.create("https://gist.github.com/");
        Mockito.when(chatDao.findById(any())).thenReturn(Optional.empty());

        assertThrows(ChatNotExistsException.class, () -> linkService.add(0L, url));
        assertThrows(ChatNotExistsException.class, () -> linkService.remove(0L, url));
        assertThrows(ChatNotExistsException.class, () -> linkService.listAll(0L));
    }

    @Test
    @DisplayName("При удалении подписки на ссылку нет")
    void remove_shouldThrowExceptionIfLinkNotSubscribe() {
        URI url = URI.create("https://gist.github.com/");
        ChatEntity chat = new ChatEntity(0L);
        LinkEntity link = new LinkEntity(url);
        Mockito.when(chatDao.findById(any())).thenReturn(Optional.of(chat));
        Mockito.when(linkDao.findByUrl(any())).thenReturn(Optional.of(link));
        Mockito.when(chatLinkDao.deleteByChatAndLink(any(), any())).thenReturn(0L);

        assertThrows(LinkNotFoundException.class, () -> linkService.remove(0L, url));
    }

    @Test
    @DisplayName("При добавлении подписка на ссылку уже есть")
    void add_shouldThrowExceptionIfLinkSubscribe() {
        URI url = URI.create("https://gist.github.com/");
        ChatEntity chat = new ChatEntity(0L);
        LinkEntity link = new LinkEntity(url);
        Mockito.when(chatDao.findById(any())).thenReturn(Optional.of(chat));
        Mockito.when(linkDao.findByUrl(any())).thenReturn(Optional.of(link));
        Mockito.when(chatLinkDao.existsByChatAndLink(any(), any())).thenReturn(true);

        assertThrows(LinkAlreadyExistsException.class, () -> linkService.add(0L, url));
    }

    @Test
    @DisplayName("При удалении подписка на ссылку есть")
    void remove_shouldNotThrowExceptionIfLinkSubscribe() {
        URI url = URI.create("https://gist.github.com/");
        Link expected = new Link(null, url, OffsetDateTime.now(), null);
        ChatEntity chat = new ChatEntity(0L);
        LinkEntity link = new LinkEntity(url);
        link.setLastCheckAt(expected.lastCheckAt());
        Mockito.when(chatDao.findById(any())).thenReturn(Optional.of(chat));
        Mockito.when(linkDao.findByUrl(any())).thenReturn(Optional.of(link));
        Mockito.when(chatLinkDao.deleteByChatAndLink(any(), any())).thenReturn(1L);

        Link result = assertDoesNotThrow(() -> linkService.remove(0L, url));

        assertThat(result)
            .isEqualTo(expected);
    }

    @Test
    @DisplayName("При добавлении подписки нет")
    void add_shouldNotThrowExceptionIfLinkNotSubscribed() {
        URI url = URI.create("https://gist.github.com/");
        Link expected = new Link(null, url, OffsetDateTime.now(), null);
        ChatEntity chat = new ChatEntity(0L);
        LinkEntity link = new LinkEntity(url);
        link.setLastCheckAt(expected.lastCheckAt());
        Mockito.when(chatDao.findById(any())).thenReturn(Optional.of(chat));
        Mockito.when(linkDao.findByUrl(any())).thenReturn(Optional.of(link));
        Mockito.when(chatLinkDao.existsByChatAndLink(any(), any())).thenReturn(false);

        Link result = assertDoesNotThrow(() -> linkService.add(0L, url));

        assertThat(result)
            .isEqualTo(expected);
    }

    @Test
    @DisplayName("Список ссылок")
    void listAll_shouldReturnAllLinks() {
        ChatEntity chat = new ChatEntity(0L);
        List<Link> links = List.of(
            new Link(null, URI.create("https://gist.github.com/"), OffsetDateTime.now(), null),
            new Link(null, URI.create("https://github.com/"), OffsetDateTime.now(), null)
        );
        Mockito.when(chatDao.findById(any())).thenReturn(Optional.of(chat));
        Mockito.when(chatLinkDao.findAllByChat(chat)).thenReturn(links.stream().map(link -> {
            LinkEntity linkEntity = new LinkEntity(link.url());
            linkEntity.setLastCheckAt(link.lastCheckAt());
            return new ChatLinkEntity(chat, linkEntity);
        }).toList());

        List<Link> result = assertDoesNotThrow(() -> linkService.listAll(0L)).stream().toList();

        assertThat(result)
            .isEqualTo(links);
    }
}
