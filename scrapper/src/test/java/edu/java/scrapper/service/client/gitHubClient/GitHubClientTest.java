package edu.java.scrapper.service.client.gitHubClient;

import com.github.tomakehurst.wiremock.junit5.WireMockRuntimeInfo;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import edu.java.scrapper.configuration.ClientConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.net.URI;
import java.time.OffsetDateTime;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.okJson;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static org.assertj.core.api.Assertions.assertThat;

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
}
