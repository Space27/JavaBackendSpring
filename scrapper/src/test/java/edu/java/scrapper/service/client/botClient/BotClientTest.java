package edu.java.scrapper.service.client.botClient;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.junit5.WireMockRuntimeInfo;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import edu.java.scrapper.configuration.ClientConfiguration;
import edu.java.scrapper.controller.response.ApiErrorResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.net.URI;
import java.util.List;
import static com.github.tomakehurst.wiremock.client.WireMock.badRequest;
import static com.github.tomakehurst.wiremock.client.WireMock.ok;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

@WireMockTest
class BotClientTest {

    BotClient client;

    @BeforeEach
    void init(WireMockRuntimeInfo wm) {
        ClientConfiguration clientConfiguration = new ClientConfiguration();

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
        stubFor(post("/updates").willReturn(badRequest().withBody(asJsonString(expected))));

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

        ApiErrorResponse result = assertThrows(
            ResponseErrorException.class,
            () -> client.updateLink(-1L, URI.create("https://github.com/"), "desc", List.of())
        )
            .getApiErrorResponse();

        assertThat(result)
            .isNull();
    }

    public static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
