package edu.java.scrapper.service.client.gitHub;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.github.tomakehurst.wiremock.junit5.WireMockRuntimeInfo;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import edu.java.scrapper.configuration.ApplicationConfig;
import edu.java.scrapper.configuration.ClientConfiguration;
import edu.java.scrapper.service.client.gitHub.dto.IssueResponse;
import edu.java.scrapper.service.client.gitHub.dto.RepositoryResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import java.net.URI;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.List;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.okJson;
import static com.github.tomakehurst.wiremock.client.WireMock.status;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.stubbing.Scenario.STARTED;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

@WireMockTest
class GitHubClientTest {

    private static final Integer maxAttempts = 2;
    GitHubClient client;

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
            )
        );
        ClientConfiguration clientConfiguration = new ClientConfiguration(applicationConfig);

        client = clientConfiguration.gitHubClient(wm.getHttpBaseUrl());
    }

    @Test
    @DisplayName("Ответ не должен быть null")
    void fetchRepository_shouldReturnNotNullResponse() {
        stubFor(get("/repos/owner/repo")
            .willReturn(okJson(
                "{\"name\": \"test\", " +
                    "\"description\": \"test\", " +
                    "\"html_url\": \"https://github.com/test/test\", " +
                    "\"updated_at\": \"2024-02-05T20:00:06Z\"}")));

        RepositoryResponse response = client.fetchRepository("owner", "repo");

        assertThat(response)
            .isNotNull();
    }

    @Test
    @DisplayName("Ответ должен совпадать с ожидаемым")
    void fetchRepository_shouldReturnCorrectlyParsedResponse() {
        stubFor(get("/repos/owner/repo")
            .willReturn(okJson(
                "{\"name\": \"Jeff Waters\", " +
                    "\"description\": \"Annihilator\", " +
                    "\"html_url\": \"https://www.annihilatormetal.com/\", " +
                    "\"updated_at\": \"2022-12-28T20:00:06Z\"}")));

        RepositoryResponse response = client.fetchRepository("owner", "repo");
        RepositoryResponse expected = new RepositoryResponse(
            "Jeff Waters",
            "Annihilator",
            URI.create("https://www.annihilatormetal.com/"),
            OffsetDateTime.parse("2022-12-28T20:00:06Z")
        );

        assertThat(response)
            .isNotNull()
            .isEqualTo(expected);
    }

    @Test
    @DisplayName("Корректный запрос тикетов")
    void fetchTickets_shouldReturnCorrectIssues() {
        OffsetDateTime base = OffsetDateTime.now(ZoneId.of("UTC"));
        List<IssueResponse> issueResponses = List.of(
            new IssueResponse("1", base),
            new IssueResponse("3", base.minusDays(1)),
            new IssueResponse("2", base.plusDays(1))
        );

        stubFor(get("/repos/owner/repo/issues")
            .willReturn(okJson(asJsonString(issueResponses))));

        List<IssueResponse> result = client.fetchIssues("owner", "repo");

        assertThat(result)
            .isEqualTo(issueResponses);
    }

    @Test
    @DisplayName("Некорректный запрос тикетов")
    void fetchTickets_shouldThrowExceptionForIncorrectRequest() {
        assertThrows(RuntimeException.class, () -> client.fetchIssues("error", "repo"));
    }

    @Test
    @DisplayName("Ответ сервера предполагает retry, retry успешен")
    void call_shouldRetryRequestForSpecificResponseCodeAndCompleteSuccessfulIfItNeededMaxRetries() {
        for (int i = 0; i < maxAttempts; ++i) {
            stubFor(get("/repos/owner/repo")
                .willReturn(status(502)).inScenario("Test")
                .whenScenarioStateIs(i == 0 ? STARTED : String.valueOf(i - 1))
                .willSetStateTo(String.valueOf(i)));
        }
        stubFor(get("/repos/owner/repo")
            .willReturn(okJson(
                "{\"name\": \"test\", " +
                    "\"description\": \"test\", " +
                    "\"html_url\": \"https://github.com/test/test\", " +
                    "\"updated_at\": \"2024-02-05T20:00:06Z\"}")).inScenario("Test")
            .whenScenarioStateIs(String.valueOf(maxAttempts - 1)));

        assertDoesNotThrow(() -> client.fetchRepository("owner", "repo"));
    }

    @Test
    @DisplayName("Ответ сервера предполагает retry, retry неуспешен")
    void call_shouldRetryRequestForSpecificResponseCodeAndCompleteUnsuccessfulIfItNeededToMoreThanMaxRetries() {
        for (int i = 0; i <= maxAttempts; ++i) {
            stubFor(get("/repos/owner/repo")
                .willReturn(status(502)).inScenario("Test")
                .whenScenarioStateIs(i == 0 ? STARTED : String.valueOf(i - 1))
                .willSetStateTo(String.valueOf(i)));
        }
        stubFor(get("/repos/owner/repo")
            .willReturn(okJson(
                "{\"name\": \"test\", " +
                    "\"description\": \"test\", " +
                    "\"html_url\": \"https://github.com/test/test\", " +
                    "\"updated_at\": \"2024-02-05T20:00:06Z\"}")).inScenario("Test")
            .whenScenarioStateIs(String.valueOf(maxAttempts)));

        Integer statusCode =
            assertThrows(WebClientResponseException.class, () -> client.fetchRepository("owner", "repo"))
                .getStatusCode().value();

        assertThat(statusCode)
            .isEqualTo(502);
    }

    public static String asJsonString(final Object obj) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());
            return objectMapper.writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
