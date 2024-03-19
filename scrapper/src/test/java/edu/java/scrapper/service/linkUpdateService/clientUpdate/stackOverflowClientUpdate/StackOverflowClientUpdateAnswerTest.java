package edu.java.scrapper.service.linkUpdateService.clientUpdate.stackOverflowClientUpdate;

import edu.java.scrapper.service.client.stackOverflowClient.StackOverflowClient;
import edu.java.scrapper.service.client.stackOverflowClient.dto.AnswerListResponse;
import java.net.URI;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mockito;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;

class StackOverflowClientUpdateAnswerTest {

    StackOverflowClientUpdateAnswerService clientUpdateService;
    StackOverflowClient client;

    @BeforeEach
    void init() {
        client = Mockito.mock(StackOverflowClient.class);
        clientUpdateService = new StackOverflowClientUpdateAnswerService(client);
    }

    @ParameterizedTest
    @ValueSource(strings = {"https://stackoverflow.com/questions/24117204/algorithm-for-simple-squared-squares",
        "https://stackoverflow.com/questions/24117204/", "https://stackoverflow.com/questions/24117204",
        "https://www.stackoverflow.com/questions/24117204/algorithm-for-simple-squared-squares",
        "https://stackoverflow.com/questions/2/algorithm-for-simple-squared-squares",
        "stackoverflow.com/questions/24117204/algorithm-for-simple-squared-squares"})
    @DisplayName("Поддерживаемые ссылки")
    void supports_shouldReturnTrueForSupportedLink(String link) {
        URI uri = URI.create(link);

        boolean supports = clientUpdateService.supports(uri);

        assertThat(supports)
            .isTrue();
    }

    @ParameterizedTest
    @ValueSource(strings = {"https://stackoverflow.com/questions/",
        "https://stackoverflow.co/questions/24117204/", "https://stackoverflow.com/question/24117204/",
        "https://stackoverflowcom/questions/24117204/",
        "https://stackoverflo.com/questions/24117204/",
        "https://tackoverflow.com/questions/24117204/"})
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
        String link = "https://stackoverflow.com/questions/24117204";
        Mockito.when(client.fetchAnswers(any())).thenThrow(WebClientResponseException.class);
        URI uri = URI.create(link);

        boolean supports = clientUpdateService.supports(uri);

        assertThat(supports)
            .isFalse();
    }

    @Test
    @DisplayName("Запрос ответов")
    void handle_shouldReturnNotEmptyStringForUpdate() {
        String link = "https://stackoverflow.com/questions/24117204";
        URI uri = URI.create(link);
        OffsetDateTime time = OffsetDateTime.now();
        List<AnswerListResponse.AnswerResponse> answerResponses = List.of(
            new AnswerListResponse.AnswerResponse(1,  time.minusNanos(1)),
            new AnswerListResponse.AnswerResponse(3, time),
            new AnswerListResponse.AnswerResponse(-2, time.plusNanos(1))
        );
        Mockito.when(client.fetchAnswers(any())).thenReturn(new AnswerListResponse(answerResponses));

        Map<String, OffsetDateTime> answer = clientUpdateService.handle(uri, time.minusNanos(1));

        assertThat(answer)
            .isNotEmpty()
            .hasSize(2)
            .containsValues(time, time.plusNanos(1));
    }
}
