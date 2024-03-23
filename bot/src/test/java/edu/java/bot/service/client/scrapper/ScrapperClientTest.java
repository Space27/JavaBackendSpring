package edu.java.bot.service.client.scrapper;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.junit5.WireMockRuntimeInfo;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import edu.java.bot.configuration.ApplicationConfig;
import edu.java.bot.configuration.ClientConfiguration;
import edu.java.bot.controller.response.ApiErrorResponse;
import edu.java.bot.service.client.scrapper.response.LinkResponse;
import edu.java.bot.service.client.scrapper.response.ListLinkResponse;
import java.net.URI;
import java.time.Duration;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import static com.github.tomakehurst.wiremock.client.WireMock.badRequest;
import static com.github.tomakehurst.wiremock.client.WireMock.containing;
import static com.github.tomakehurst.wiremock.client.WireMock.delete;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.ok;
import static com.github.tomakehurst.wiremock.client.WireMock.okJson;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.status;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.stubbing.Scenario.STARTED;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

@WireMockTest
class ScrapperClientTest {

    private static final Integer maxAttempts = 2;

    ScrapperClient client;

    @BeforeEach
    void init(WireMockRuntimeInfo wm) {
        ApplicationConfig applicationConfig = new ApplicationConfig(
            null,
            null,
            new ApplicationConfig.RetryConfig(
                maxAttempts,
                ApplicationConfig.RetryConfig.DelayType.FIXED,
                Duration.ofSeconds(1),
                List.of(502)
            )
        );
        ClientConfiguration clientConfiguration = new ClientConfiguration(applicationConfig);

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
            .willReturn(badRequest()
                .withHeader("Content-Type", "application/json")
                .withBody(asJsonString(expected))));

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
            .willReturn(badRequest().withHeader("Content-Type", "application/json").withBody("")));

        Integer statusCode = assertThrows(WebClientResponseException.class, () -> client.addChat(1L))
            .getStatusCode().value();

        assertThat(statusCode)
            .isEqualTo(400);
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
        stubFor(delete("/tg-chat/1").willReturn(badRequest()
            .withHeader("Content-Type", "application/json")
            .withBody(asJsonString(expected))));

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

        ListLinkResponse answer = Assertions.assertDoesNotThrow(() -> client.getLinks(3L));

        assertThat(answer)
            .isEqualTo(expected);
    }

    @Test
    @DisplayName("Корректный запрос добавления ссылки")
    void addLink_shouldWorkWithCorrectRequest() {
        LinkResponse expected = new LinkResponse(1L, URI.create("https://github.com/"));
        stubFor(post("/links").withHeader("Tg-Chat-Id", containing("3")).willReturn(okJson(asJsonString(expected))));

        LinkResponse answer =
            Assertions.assertDoesNotThrow(() -> client.addLink(3L, URI.create("https://github.com/")));

        assertThat(answer)
            .isEqualTo(expected);
    }

    @Test
    @DisplayName("Корректный запрос удаления ссылки")
    void removeLink_shouldWorkWithCorrectRequest() {
        LinkResponse expected = new LinkResponse(1L, URI.create("https://github.com/"));
        stubFor(delete("/links").withHeader("Tg-Chat-Id", containing("3")).willReturn(okJson(asJsonString(expected))));

        LinkResponse answer =
            Assertions.assertDoesNotThrow(() -> client.removeLink(3L, URI.create("https://github.com/")));

        assertThat(answer)
            .isEqualTo(expected);
    }

    @Test
    @DisplayName("Ответ сервера предполагает retry, retry успешен")
    void call_shouldRetryRequestForSpecificResponseCodeAndCompleteSuccessfulIfItNeededMaxRetries() {
        for (int i = 0; i < maxAttempts; ++i) {
            stubFor(post("/tg-chat/1").willReturn(status(502)).inScenario("Test")
                .whenScenarioStateIs(i == 0 ? STARTED : String.valueOf(i - 1))
                .willSetStateTo(String.valueOf(i)));
        }
        stubFor(post("/tg-chat/1").willReturn(ok()).inScenario("Test")
            .whenScenarioStateIs(String.valueOf(maxAttempts - 1)));

        assertDoesNotThrow(() -> client.addChat(1L));
    }

    @Test
    @DisplayName("Ответ сервера предполагает retry, retry неуспешен")
    void call_shouldRetryRequestForSpecificResponseCodeAndCompleteUnsuccessfulIfItNeededToMoreThanMaxRetries() {
        for (int i = 0; i <= maxAttempts; ++i) {
            stubFor(post("/tg-chat/1").willReturn(status(502)).inScenario("Test")
                .whenScenarioStateIs(i == 0 ? STARTED : String.valueOf(i - 1))
                .willSetStateTo(String.valueOf(i)));
        }
        stubFor(post("/tg-chat/1").willReturn(ok()).inScenario("Test")
            .whenScenarioStateIs(String.valueOf(maxAttempts)));

        Integer statusCode = assertThrows(WebClientResponseException.class, () -> client.addChat(1L))
            .getStatusCode().value();

        assertThat(statusCode)
            .isEqualTo(502);
    }

    public static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
