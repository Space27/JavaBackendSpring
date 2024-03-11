package edu.java.scrapper.service.linkUpdateService.clientUpdate;

import edu.java.scrapper.service.client.gitHubClient.GitHubClient;
import edu.java.scrapper.service.client.gitHubClient.RepositoryResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mockito;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import java.net.URI;
import java.time.OffsetDateTime;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;

public class GitHubClientUpdateTest {

    GitHubClientUpdateService clientUpdateService;
    GitHubClient client;

    @BeforeEach
    void init() {
        client = Mockito.mock(GitHubClient.class);
        clientUpdateService = new GitHubClientUpdateService(client);
    }

    @ParameterizedTest
    @ValueSource(strings = {"https://github.com/Space27/JavaBackendSpring/pulls",
        "https://github.com/Space27/JavaBackendSpring/", "https://github.com/Space27/JavaBackendSpring",
        "https://www.github.com/Space27/JavaBackendSpring/pulls",
        "github.com/Space27/JavaBackendSpring/pulls",
        "https://www.github.com/Space27/JavaBackendSpring/pulls",
        "https://github.com/S/J/pulls"})
    @DisplayName("Поддерживаемые ссылки")
    void supports_shouldReturnTrueForSupportedLink(String link) {
        URI uri = URI.create(link);

        boolean supports = clientUpdateService.supports(uri);

        assertThat(supports)
            .isTrue();
    }

    @ParameterizedTest
    @ValueSource(strings = {"https://github.co/Space27/JavaBackendSpring/pulls",
        "https://githu.com/Space27/JavaBackendSpring/", "https://ithub.com/Space27/JavaBackendSpring",
        "https://www.githubcom/Space27/JavaBackendSpring/pulls",
        "github.com/Space27/",
        "https://www.github.com//JavaBackendSpring/pulls",
        "https://github.com/S"})
    @DisplayName("Неподдерживаемый формат ссылки")
    void supports_shouldReturnFalseForUnsupportedFormatLink(String link) {
        URI uri = URI.create(link);

        boolean supports = clientUpdateService.supports(uri);

        assertThat(supports)
            .isFalse();
    }

    @Test
    @DisplayName("Поддерживаемые ссылки по формату, но запрос не дает ответ")
    void supports_shouldReturnFalseForSupportedLinkButWithoutResponse() {
        String link = "https://github.com/Space27/JavaBackendSpring";
        Mockito.when(client.fetchRepository(any(), any())).thenThrow(WebClientResponseException.class);
        URI uri = URI.create(link);

        boolean supports = clientUpdateService.supports(uri);

        assertThat(supports)
            .isFalse();
    }

    @Test
    @DisplayName("Есть обновление")
    void handle_shouldReturnNotEmptyStringForUpdate() {
        String link = "https://github.com/Space27/JavaBackendSpring";
        URI uri = URI.create(link);
        OffsetDateTime time = OffsetDateTime.now();
        RepositoryResponse response = new RepositoryResponse("fr", "fr", uri, time);
        Mockito.when(client.fetchRepository(any(), any())).thenReturn(response);

        String answer = clientUpdateService.handle(uri, time.minusNanos(1));

        assertThat(answer)
            .isNotEmpty();
    }

    @Test
    @DisplayName("Нет обновления")
    void handle_shouldReturnNullForNotUpdate() {
        String link = "https://github.com/Space27/JavaBackendSpring";
        URI uri = URI.create(link);
        OffsetDateTime time = OffsetDateTime.now();
        RepositoryResponse response = new RepositoryResponse("fr", "fr", uri, time);
        Mockito.when(client.fetchRepository(any(), any())).thenReturn(response);

        String answer = clientUpdateService.handle(uri, time);

        assertThat(answer)
            .isNull();
    }
}
