package edu.java.bot.service.clients.ScrapperClient;

import com.github.tomakehurst.wiremock.junit5.WireMockRuntimeInfo;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import edu.java.bot.configuration.ClientConfiguration;
import edu.java.bot.service.api.schemas.ApiErrorResponse;
import edu.java.bot.service.clients.ScrapperClient.schemas.LinkResponse;
import edu.java.bot.service.clients.ScrapperClient.schemas.ListLinkResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URI;
import java.util.List;
import static com.github.tomakehurst.wiremock.client.WireMock.badRequest;
import static com.github.tomakehurst.wiremock.client.WireMock.containing;
import static com.github.tomakehurst.wiremock.client.WireMock.delete;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.ok;
import static com.github.tomakehurst.wiremock.client.WireMock.okJson;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

@WireMockTest
class ScrapperClientTest {

    ScrapperClient client;

    @BeforeEach
    void init(WireMockRuntimeInfo wm) {
        ClientConfiguration clientConfiguration = new ClientConfiguration();

        client = clientConfiguration.scrapperClient(wm.getHttpBaseUrl());
    }

    @Test
    @DisplayName("Регистрация чата с корректным запросом")
    void addChat_shouldWorkWithCorrectRequest() {
        stubFor(post("/tg-chat/1").willReturn(ok()));

        assertDoesNotThrow(() -> client.addChat(1L));
    }

    @Test
    @DisplayName("Регистрация чата с некорректным запросом")
    void addChat_shouldWorkWithIncorrectRequestAndThrowExceptionWithApiErrorResponse() {
        ApiErrorResponse expected = new ApiErrorResponse("desc", "code", "excep", "message", List.of());
        stubFor(post("/tg-chat/1")
            .willReturn(badRequest().withBody(asJsonString(expected))));

        ApiErrorResponse result = assertThrows(ResponseErrorException.class, () -> client.addChat(1L))
            .getApiErrorResponse();

        assertThat(result)
            .isNotNull()
            .isEqualTo(expected);
    }

    @Test
    @DisplayName("Регистрация чата с некорректным ответом ошибки")
    void addChat_shouldWorkWithIncorrectRequestAndThrowExceptionWithNullForUnexpectedAnswer() {
        stubFor(post("/tg-chat/1")
            .willReturn(badRequest().withBody("")));

        ApiErrorResponse result = assertThrows(ResponseErrorException.class, () -> client.addChat(1L))
            .getApiErrorResponse();

        assertThat(result)
            .isNull();
    }

    @Test
    @DisplayName("Корректный запрос удаления чата")
    void deleteChat_shouldWorkWithCorrectRequest() {
        stubFor(delete("/tg-chat/1").willReturn(ok()));

        assertDoesNotThrow(() -> client.deleteChat(1L));
    }

    @Test
    @DisplayName("Некорректный запрос удаления чата")
    void deleteChat_shouldWorkWithIncorrectRequestAndThrowExceptionWithApiErrorResponse() {
        ApiErrorResponse expected = new ApiErrorResponse("desc", "code", "excep", "message", List.of());
        stubFor(delete("/tg-chat/1").willReturn(badRequest().withBody(asJsonString(expected))));

        ApiErrorResponse result = assertThrows(ResponseErrorException.class, () -> client.deleteChat(1L))
            .getApiErrorResponse();

        assertThat(result)
            .isNotNull()
            .isEqualTo(expected);
    }

    @Test
    @DisplayName("Корректный запрос получения ссылок")
    void getLinks_shouldWorkWithCorrectRequest() {
        ListLinkResponse expected = new ListLinkResponse(List.of(
            new LinkResponse(1L, URI.create("https://github.com/")),
            new LinkResponse(2L, URI.create("http://github.com/"))
        ), 2);
        stubFor(get("/links").withHeader("Tg-Chat-Id", containing("3")).willReturn(okJson(asJsonString(expected))));

        ListLinkResponse answer = assertDoesNotThrow(() -> client.getLinks(3L));

        assertThat(answer)
            .isEqualTo(expected);
    }

    @Test
    @DisplayName("Корректный запрос добавления ссылки")
    void addLink_shouldWorkWithCorrectRequest() {
        LinkResponse expected = new LinkResponse(1L, URI.create("https://github.com/"));
        stubFor(post("/links").withHeader("Tg-Chat-Id", containing("3")).willReturn(okJson(asJsonString(expected))));

        LinkResponse answer = assertDoesNotThrow(() -> client.addLink(3L, URI.create("https://github.com/")));

        assertThat(answer)
            .isEqualTo(expected);
    }

    @Test
    @DisplayName("Корректный запрос удаления ссылки")
    void removeLink_shouldWorkWithCorrectRequest() {
        LinkResponse expected = new LinkResponse(1L, URI.create("https://github.com/"));
        stubFor(delete("/links").withHeader("Tg-Chat-Id", containing("3")).willReturn(okJson(asJsonString(expected))));

        LinkResponse answer = assertDoesNotThrow(() -> client.removeLink(3L, URI.create("https://github.com/")));

        assertThat(answer)
            .isEqualTo(expected);
    }

    public static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
