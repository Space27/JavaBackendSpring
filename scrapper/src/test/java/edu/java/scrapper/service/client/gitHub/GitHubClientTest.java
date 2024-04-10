package edu.java.scrapper.service.client.gitHub;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.github.tomakehurst.wiremock.junit5.WireMockRuntimeInfo;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import edu.java.scrapper.configuration.ClientConfiguration;
import edu.java.scrapper.service.client.gitHub.dto.IssueResponse;
import edu.java.scrapper.service.client.gitHub.dto.RepositoryResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.net.URI;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.List;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.okJson;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@WireMockTest
class GitHubClientTest {

    GitHubClient client;

    @BeforeEach
    void init(WireMockRuntimeInfo wm) {
        ClientConfiguration clientConfiguration = new ClientConfiguration();

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
