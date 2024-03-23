package edu.java.scrapper.service.client.stackOverflowClient;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.github.tomakehurst.wiremock.junit5.WireMockRuntimeInfo;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import edu.java.scrapper.configuration.ApplicationConfig;
import edu.java.scrapper.configuration.ClientConfiguration;
import edu.java.scrapper.service.client.stackOverflowClient.dto.AnswerListResponse;
import edu.java.scrapper.service.client.stackOverflowClient.dto.QuestionResponse;
import java.net.URI;
import java.time.Duration;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.okJson;
import static com.github.tomakehurst.wiremock.client.WireMock.status;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.stubbing.Scenario.STARTED;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

@WireMockTest
class StackOverflowClientTest {

    private static final Integer maxAttempts = 2;
    StackOverflowClient client;

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

        client = clientConfiguration.stackOverflowClient(wm.getHttpBaseUrl());
    }

    @Test
    @DisplayName("Ответ не должен быть null")
    void fetchQuestion_shouldReturnNotNullResponse() {
        stubFor(get("/questions/10?site=stackoverflow")
            .willReturn(okJson(
                "{\"items\":[{" +
                    "\"last_activity_date\": 120," +
                    "\"link\": \"https://stackoverflow.com/questions/10/\", " +
                    "\"title\": \"What exactly is the meaning of an API?\", " +
                    "\"answer_count\": 13}]}")));

        QuestionResponse response = client.fetchQuestion(10L);

        assertThat(response)
            .isNotNull();
    }

    @Test
    @DisplayName("Ответ должен совпадать с ожидаемым")
    void fetchQuestion_shouldReturnCorrectlyParsedResponse() {
        stubFor(get("/questions/10?site=stackoverflow")
            .willReturn(okJson(
                "{\"items\":[{" +
                    "\"last_activity_date\": 120634435534," +
                    "\"link\": \"https://pantera.com/\", " +
                    "\"title\": \"Pantera\", " +
                    "\"answer_count\": 25000000}]}")));

        QuestionResponse response = client.fetchQuestion(10L);
        QuestionResponse expected = new QuestionResponse(List.of(new QuestionResponse.ItemResponse(
            "Pantera",
            URI.create("https://pantera.com/"),
            25_000_000,
            OffsetDateTime.ofInstant(Instant.ofEpochSecond(120634435534L), ZoneId.of("UTC"))
        )));

        assertThat(response)
            .isNotNull()
            .isEqualTo(expected);
    }

    @Test
    @DisplayName("Корректный запрос ответов на вопрос")
    void fetchAnswers_shouldReturnCorrectAnswers() {
        OffsetDateTime base = OffsetDateTime.now(ZoneId.of("UTC"));
        List<AnswerListResponse.AnswerResponse> answerResponses = List.of(
            new AnswerListResponse.AnswerResponse(1, base),
            new AnswerListResponse.AnswerResponse(3, base.minusDays(1)),
            new AnswerListResponse.AnswerResponse(-2, base.plusDays(1))
        );
        stubFor(get("/questions/10/answers?site=stackoverflow")
            .willReturn(okJson(asJsonString(new AnswerListResponse(answerResponses)))));

        List<AnswerListResponse.AnswerResponse> result = client.fetchAnswers(10L).items();

        assertThat(result)
            .isEqualTo(answerResponses);
    }

    @Test
    @DisplayName("Некорректный запрос ответов")
    void fetchAnswers_shouldThrowExceptionForIncorrectRequest() {
        assertThrows(RuntimeException.class, () -> client.fetchAnswers(1L));
    }

    @Test
    @DisplayName("Ответ сервера предполагает retry, retry успешен")
    void call_shouldRetryRequestForSpecificResponseCodeAndCompleteSuccessfulIfItNeededMaxRetries() {
        for (int i = 0; i < maxAttempts; ++i) {
            stubFor(get("/questions/10?site=stackoverflow")
                .willReturn(status(502)).inScenario("Test")
                .whenScenarioStateIs(i == 0 ? STARTED : String.valueOf(i - 1))
                .willSetStateTo(String.valueOf(i)));
        }
        stubFor(get("/questions/10?site=stackoverflow")
            .willReturn(okJson(
                "{\"items\":[{" +
                    "\"last_activity_date\": 120," +
                    "\"link\": \"https://stackoverflow.com/questions/10/\", " +
                    "\"title\": \"What exactly is the meaning of an API?\", " +
                    "\"answer_count\": 13}]}")).inScenario("Test")
            .whenScenarioStateIs(String.valueOf(maxAttempts - 1)));

        assertDoesNotThrow(() -> client.fetchQuestion(10L));
    }

    @Test
    @DisplayName("Ответ сервера предполагает retry, retry неуспешен")
    void call_shouldRetryRequestForSpecificResponseCodeAndCompleteUnsuccessfulIfItNeededToMoreThanMaxRetries() {
        for (int i = 0; i <= maxAttempts; ++i) {
            stubFor(get("/questions/10?site=stackoverflow")
                .willReturn(status(502)).inScenario("Test")
                .whenScenarioStateIs(i == 0 ? STARTED : String.valueOf(i - 1))
                .willSetStateTo(String.valueOf(i)));
        }
        stubFor(get("/questions/10?site=stackoverflow")
            .willReturn(okJson(
                "{\"items\":[{" +
                    "\"last_activity_date\": 120," +
                    "\"link\": \"https://stackoverflow.com/questions/10/\", " +
                    "\"title\": \"What exactly is the meaning of an API?\", " +
                    "\"answer_count\": 13}]}")).inScenario("Test")
            .whenScenarioStateIs(String.valueOf(maxAttempts)));

        Integer statusCode = assertThrows(WebClientResponseException.class, () -> client.fetchQuestion(10L))
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
