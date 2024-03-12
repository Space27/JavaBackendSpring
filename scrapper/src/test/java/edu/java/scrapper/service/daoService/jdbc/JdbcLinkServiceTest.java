package edu.java.scrapper.service.daoService.jdbc;

import edu.java.scrapper.controller.linksApi.exception.ChatNotExistsException;
import edu.java.scrapper.controller.linksApi.exception.LinkAlreadyExistsException;
import edu.java.scrapper.controller.linksApi.exception.LinkNotFoundException;
import edu.java.scrapper.domain.dao.jdbc.JdbcChatLinkDao;
import edu.java.scrapper.domain.dao.jdbc.JdbcLinkDao;
import edu.java.scrapper.domain.dao.jdbc.JdbcTgChatDao;
import edu.java.scrapper.domain.dto.Chat;
import edu.java.scrapper.domain.dto.Link;
import java.net.URI;
import java.time.OffsetDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;

class JdbcLinkServiceTest {

    JdbcLinkService linkService;
    JdbcTgChatDao chatDao;
    JdbcLinkDao linkDao;
    JdbcChatLinkDao chatLinkDao;

    @BeforeEach
    void init() {
        chatDao = Mockito.mock(JdbcTgChatDao.class);
        linkDao = Mockito.mock(JdbcLinkDao.class);
        chatLinkDao = Mockito.mock(JdbcChatLinkDao.class);

        linkService = new JdbcLinkService(linkDao, chatDao, chatLinkDao);
    }

    @Test
    @DisplayName("Чат не существует")
    void calls_shouldThrowExceptionIfChatNotExists() {
        URI url = URI.create("https://gist.github.com/");
        Mockito.when(chatDao.findById(any())).thenReturn(null);

        assertThrows(ChatNotExistsException.class, () -> linkService.add(0L, url));
        assertThrows(ChatNotExistsException.class, () -> linkService.remove(0L, url));
        assertThrows(ChatNotExistsException.class, () -> linkService.listAll(0L));
    }

    @Test
    @DisplayName("При удалении подписки на ссылку нет")
    void remove_shouldThrowExceptionIfLinkNotSubscribe() {
        URI url = URI.create("https://gist.github.com/");
        Mockito.when(chatDao.findById(any())).thenReturn(new Chat(0L, OffsetDateTime.now()));
        Mockito.when(linkDao.find((URI) any()))
            .thenReturn(new Link(1L, url, OffsetDateTime.now(), OffsetDateTime.now()));
        Mockito.when(chatLinkDao.remove(any(), any())).thenReturn(false);

        assertThrows(LinkNotFoundException.class, () -> linkService.remove(0L, url));
    }

    @Test
    @DisplayName("При добавлении подписка на ссылку уже есть")
    void add_shouldThrowExceptionIfLinkSubscribe() {
        URI url = URI.create("https://gist.github.com/");
        Mockito.when(chatDao.findById(any())).thenReturn(new Chat(0L, OffsetDateTime.now()));
        Mockito.when(linkDao.add(any())).thenReturn(new Link(1L, url, OffsetDateTime.now(), OffsetDateTime.now()));
        Mockito.when(chatLinkDao.add(any(), any())).thenReturn(false);

        assertThrows(LinkAlreadyExistsException.class, () -> linkService.add(0L, url));
    }

    @Test
    @DisplayName("При удалении подписка на ссылку есть")
    void remove_shouldNotThrowExceptionIfLinkSubscribe() {
        URI url = URI.create("https://gist.github.com/");
        Link expected = new Link(1L, url, OffsetDateTime.now(), OffsetDateTime.now());
        Mockito.when(chatDao.findById(any())).thenReturn(new Chat(0L, OffsetDateTime.now()));
        Mockito.when(linkDao.find((URI) any())).thenReturn(expected);
        Mockito.when(chatLinkDao.remove(any(), any())).thenReturn(true);

        Link result = assertDoesNotThrow(() -> linkService.remove(0L, url));

        assertThat(result)
            .isEqualTo(expected);
    }

    @Test
    @DisplayName("При добавлении подписки нет")
    void add_shouldNotThrowExceptionIfLinkNotSubscribed() {
        URI url = URI.create("https://gist.github.com/");
        Link expected = new Link(1L, url, OffsetDateTime.now(), OffsetDateTime.now());
        Mockito.when(chatDao.findById(any())).thenReturn(new Chat(0L, OffsetDateTime.now()));
        Mockito.when(linkDao.add(any())).thenReturn(expected);
        Mockito.when(chatLinkDao.add(any(), any())).thenReturn(true);

        Link result = assertDoesNotThrow(() -> linkService.add(0L, url));

        assertThat(result)
            .isEqualTo(expected);
    }

    @Test
    @DisplayName("Список ссылок")
    void listAll_shouldReturnAllLinks() {
        List<Link> links = List.of(
            new Link(1L, URI.create("https://gist.github.com/"), OffsetDateTime.now(), OffsetDateTime.now()),
            new Link(2L, URI.create("https://github.com/"), OffsetDateTime.now(), OffsetDateTime.now())
        );
        Mockito.when(chatDao.findById(any())).thenReturn(new Chat(0L, OffsetDateTime.now()));
        Mockito.when(chatLinkDao.findLinksByChat(any())).thenReturn(links);

        List<Link> result = assertDoesNotThrow(() -> linkService.listAll(0L)).stream().toList();

        assertThat(result)
            .isEqualTo(links);
    }
}
