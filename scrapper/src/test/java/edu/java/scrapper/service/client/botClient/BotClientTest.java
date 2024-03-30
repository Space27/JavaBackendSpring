package edu.java.scrapper.service.client.botClient;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.junit5.WireMockRuntimeInfo;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import edu.java.scrapper.configuration.ApplicationConfig;
import edu.java.scrapper.configuration.ClientConfiguration;
import edu.java.scrapper.controller.response.ApiErrorResponse;
import java.net.URI;
import java.time.Duration;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import static com.github.tomakehurst.wiremock.client.WireMock.badRequest;
import static com.github.tomakehurst.wiremock.client.WireMock.ok;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.status;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.stubbing.Scenario.STARTED;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

@WireMockTest
class BotClientTest {

    private static final Integer maxAttempts = 2;
    BotClient client;

    @BeforeEach
    void init(WireMockRuntimeInfo wm) {
        ApplicationConfig applicationConfig = new ApplicationConfig(
            null,
            null,
            null,
            new ApplicationConfig.RetryConfig(
                maxAttempts,
                ApplicationConfig.RetryConfig.DelayType.FIXED,
                Duration.ofSeconds(1),
                List.of(502)
            ),
            null,
            null
        );
        ClientConfiguration clientConfiguration = new ClientConfiguration(applicationConfig);

        client = clientConfiguration.botClient(wm.getHttpBaseUrl());
    }

    @Test
    @DisplayName("Обновление с корректным запросом")
    void updateLink_shouldWorkWithCorrectRequest() {
        stubFor(post("/updates").willReturn(ok()));

        assertDoesNotThrow(() -> client.updateLink(1L, URI.create("https://github.com/"), "desc", List.of()));
    }

    @Test
    @DisplayName("Обновление с некорректным запросом")
    void updateLink_shouldWorkWithIncorrectRequestAndThrowExceptionWithResponse() {
        ApiErrorResponse expected = new ApiErrorResponse("des", "cod", "name", "mess", List.of());
        stubFor(post("/updates").willReturn(badRequest()
            .withHeader("Content-Type", "application/json")
            .withBody(asJsonString(expected))));

        ApiErrorResponse result = assertThrows(
            ResponseErrorException.class,
            () -> client.updateLink(-1L, URI.create("https://github.com/"), "desc", List.of())
        )
            .getApiErrorResponse();

        assertThat(result)
            .isEqualTo(expected);
    }

    @Test
    @DisplayName("Обновление с некорректным ответом")
    void updateLink_shouldWorkWithIncorrectResponse() {
        stubFor(post("/updates").willReturn(badRequest().withBody("")));

        Integer statusCode = assertThrows(
            WebClientResponseException.class,
            () -> client.updateLink(1L, URI.create("https://github.com/"), "desc", List.of())
        )
            .getStatusCode().value();

        assertThat(statusCode)
            .isEqualTo(400);
    }

    @Test
    @DisplayName("Ответ сервера предполагает retry, retry успешен")
    void call_shouldRetryRequestForSpecificResponseCodeAndCompleteSuccessfulIfItNeededMaxRetries() {
        for (int i = 0; i < maxAttempts; ++i) {
            stubFor(post("/updates").willReturn(status(502)).inScenario("Test")
                .whenScenarioStateIs(i == 0 ? STARTED : String.valueOf(i - 1))
                .willSetStateTo(String.valueOf(i)));
        }
        stubFor(post("/updates").willReturn(ok()).inScenario("Test")
            .whenScenarioStateIs(String.valueOf(maxAttempts - 1)));

        assertDoesNotThrow(() -> client.updateLink(1L, URI.create("https://github.com/"), "desc", List.of()));
    }

    @Test
    @DisplayName("Ответ сервера предполагает retry, retry неуспешен")
    void call_shouldRetryRequestForSpecificResponseCodeAndCompleteUnsuccessfulIfItNeededToMoreThanMaxRetries() {
        for (int i = 0; i <= maxAttempts; ++i) {
            stubFor(post("/updates").willReturn(status(502)).inScenario("Test")
                .whenScenarioStateIs(i == 0 ? STARTED : String.valueOf(i - 1))
                .willSetStateTo(String.valueOf(i)));
        }
        stubFor(post("/updates").willReturn(ok()).inScenario("Test")
            .whenScenarioStateIs(String.valueOf(maxAttempts)));

        Integer statusCode = assertThrows(
            WebClientResponseException.class,
            () -> client.updateLink(1L, URI.create("https://github.com/"), "desc", List.of())
        )
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
