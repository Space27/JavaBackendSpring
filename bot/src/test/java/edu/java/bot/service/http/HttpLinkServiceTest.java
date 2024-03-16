package edu.java.bot.service.http;

import edu.java.bot.controller.response.ApiErrorResponse;
import edu.java.bot.service.LinkService;
import edu.java.bot.service.client.scrapper.ResponseErrorException;
import edu.java.bot.service.client.scrapper.ScrapperClient;
import edu.java.bot.service.client.scrapper.response.LinkResponse;
import edu.java.bot.service.client.scrapper.response.ListLinkResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import java.net.URI;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;

class HttpLinkServiceTest {

    HttpLinkService linkService;
    ScrapperClient scrapperClient;

    @BeforeEach
    void init() {
        scrapperClient = Mockito.mock(ScrapperClient.class);
        linkService = new HttpLinkService(scrapperClient);
    }

    @Test
    @DisplayName("Корректный запрос пустого списка ссылок")
    void getLinks_shouldReturnEmptyListForCorrectRequestWithEmptyChat() {
        Long chatId = 1L;
        Mockito.when(scrapperClient.getLinks(chatId)).thenReturn(new ListLinkResponse(List.of(), 0));

        List<URI> result = linkService.getLinks(chatId);

        assertThat(result)
            .isEmpty();
    }

    @Test
    @DisplayName("Корректный запрос списка ссылок")
    void getLinks_shouldReturnListForCorrectRequest() {
        List<URI> links = List.of(
            URI.create("https://edu.tinkoff.ru/"),
            URI.create("https://github.com/"),
            URI.create("https://lk.etu.ru/student#/")
        );
        Long chatId = 1L;
        Mockito.when(scrapperClient.getLinks(chatId))
            .thenReturn(new ListLinkResponse(
                links.stream().map(link -> new LinkResponse(0L, link)).toList(),
                links.size()
            ));

        List<URI> result = linkService.getLinks(chatId);

        assertThat(result)
            .isEqualTo(links);
    }

    @Test
    @DisplayName("Некорректный запрос списка ссылок")
    void getLinks_shouldReturnNullForIncorrectRequest() {
        Long chatId = 1L;
        Mockito.when(scrapperClient.getLinks(chatId)).thenThrow(ResponseErrorException.class);

        List<URI> result = linkService.getLinks(chatId);

        assertThat(result)
            .isNull();
    }

    @Test
    @DisplayName("Корректный запрос добавления ссылки")
    void addLink_shouldReturnOKForCorrectRequest() {
        Long chatId = 1L;
        URI link = URI.create("https://edu.tinkoff.ru/");

        LinkService.Response response = linkService.addLink(chatId, link);

        assertThat(response)
            .isEqualTo(LinkService.Response.OK);
    }

    @Test
    @DisplayName("Запрос добавления уже добавленной ссылки")
    void addLink_shouldReturnLinkAlreadyTrackingForRepeatRequest() {
        Long chatId = 1L;
        URI link = URI.create("https://edu.tinkoff.ru/");
        Mockito.when(scrapperClient.addLink(chatId, link))
            .thenThrow(new ResponseErrorException(new ApiErrorResponse("d", "409", "n", "m", List.of())));

        LinkService.Response response = linkService.addLink(chatId, link);

        assertThat(response)
            .isEqualTo(LinkService.Response.LINK_ALREADY_TRACKING);
    }

    @Test
    @DisplayName("Запрос добавления неподдерживаемой ссылки")
    void addLink_shouldReturnNotValidRequestForNotSupportedLink() {
        Long chatId = 1L;
        URI link = URI.create("https://edu.tinkoff.ru/");
        Mockito.when(scrapperClient.addLink(chatId, link))
            .thenThrow(new ResponseErrorException(new ApiErrorResponse("d", "400", "n", "m", List.of())));

        LinkService.Response response = linkService.addLink(chatId, link);

        assertThat(response)
            .isEqualTo(LinkService.Response.NOT_VALID_REQUEST);
    }

    @Test
    @DisplayName("Запрос добавления ссылки в несуществующий чат")
    void addLink_shouldReturnChatNotExistsForNotExistingChat() {
        Long chatId = 1L;
        URI link = URI.create("https://edu.tinkoff.ru/");
        Mockito.when(scrapperClient.addLink(chatId, link))
            .thenThrow(new ResponseErrorException(new ApiErrorResponse("d", "406", "n", "m", List.of())));

        LinkService.Response response = linkService.addLink(chatId, link);

        assertThat(response)
            .isEqualTo(LinkService.Response.CHAT_NOT_EXISTS);
    }

    @Test
    @DisplayName("Корректный запрос удаления ссылки")
    void removeLink_shouldReturnOKForCorrectRequest() {
        Long chatId = 1L;
        URI link = URI.create("https://edu.tinkoff.ru/");

        LinkService.Response response = linkService.removeLink(chatId, link);

        assertThat(response)
            .isEqualTo(LinkService.Response.OK);
    }

    @Test
    @DisplayName("Запрос удаления несуществующей ссылки")
    void removeLink_shouldReturnLinkNotTrackingForRemovingNotTrackingLink() {
        Long chatId = 1L;
        URI link = URI.create("https://edu.tinkoff.ru/");
        Mockito.when(scrapperClient.removeLink(chatId, link))
            .thenThrow(new ResponseErrorException(new ApiErrorResponse("d", "404", "n", "m", List.of())));

        LinkService.Response response = linkService.removeLink(chatId, link);

        assertThat(response)
            .isEqualTo(LinkService.Response.LINK_NOT_TRACKING);
    }

    @Test
    @DisplayName("Запрос удаления неподдерживаемой")
    void removeLink_shouldReturnNotValidRequestForNotSupportedLink() {
        Long chatId = 1L;
        URI link = URI.create("https://edu.tinkoff.ru/");
        Mockito.when(scrapperClient.removeLink(chatId, link))
            .thenThrow(new ResponseErrorException(new ApiErrorResponse("d", "400", "n", "m", List.of())));

        LinkService.Response response = linkService.removeLink(chatId, link);

        assertThat(response)
            .isEqualTo(LinkService.Response.NOT_VALID_REQUEST);
    }

    @Test
    @DisplayName("Запрос удаления ссылки из несуществующего чата")
    void removeLink_shouldReturnChatNotExistsForNotExistingChat() {
        Long chatId = 1L;
        URI link = URI.create("https://edu.tinkoff.ru/");
        Mockito.when(scrapperClient.removeLink(chatId, link))
            .thenThrow(new ResponseErrorException(new ApiErrorResponse("d", "406", "n", "m", List.of())));

        LinkService.Response response = linkService.removeLink(chatId, link);

        assertThat(response)
            .isEqualTo(LinkService.Response.CHAT_NOT_EXISTS);
    }
}
