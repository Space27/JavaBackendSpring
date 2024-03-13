package edu.java.scrapper.service.client.stackOverflowClient;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.github.tomakehurst.wiremock.junit5.WireMockRuntimeInfo;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import edu.java.scrapper.configuration.ClientConfiguration;
import edu.java.scrapper.service.client.stackOverflowClient.dto.AnswerListResponse;
import edu.java.scrapper.service.client.stackOverflowClient.dto.QuestionResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.net.URI;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.List;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.okJson;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@WireMockTest
class StackOverflowClientTest {

    StackOverflowClient client;

    @BeforeEach
    void init(WireMockRuntimeInfo wm) {
        ClientConfiguration clientConfiguration = new ClientConfiguration();

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
            new AnswerListResponse.AnswerResponse(1,  base),
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
