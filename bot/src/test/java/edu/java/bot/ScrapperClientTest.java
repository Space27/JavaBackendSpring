package edu.java.bot;

import com.github.tomakehurst.wiremock.junit5.WireMockRuntimeInfo;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import edu.java.bot.configuration.ApplicationConfig;
import edu.java.bot.configuration.ClientConfiguration;
import edu.java.bot.service.api.schemas.ApiErrorResponse;
import edu.java.bot.service.scrapperClient.ResponseErrorException;
import edu.java.bot.service.scrapperClient.ScrapperClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import static com.github.tomakehurst.wiremock.client.WireMock.jsonResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.ok;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

@WireMockTest
class ScrapperClientTest {

    ScrapperClient client;

    @BeforeEach
    void init(WireMockRuntimeInfo wm) {
        ApplicationConfig applicationConfig =
            new ApplicationConfig(null, new ApplicationConfig.Scrapper(wm.getHttpBaseUrl()));
        ClientConfiguration clientConfiguration = new ClientConfiguration();

        client = clientConfiguration.scrapperClient(applicationConfig);
    }

    @Test
    @DisplayName("Регистрация чата с корректным запросом")
    void addChat_shouldWorkWithCorrectRequest() {
        stubFor(post("/tg-chat/1").willReturn(ok()));

        assertDoesNotThrow(() -> client.addChat(1L));
    }

    /*@Test
    @DisplayName("Регистрация чата с некорректным запросом")
    void addChat_shouldWorkWithIncorrectRequestAndThrowExceptionWithApiErrorResponse() {
        ApiErrorResponse apiErrorResponse = new ApiErrorResponse("desc", "code", "excep", "message", List.of());
        stubFor(post("/tg-chat/1").willReturn(jsonResponse(asJsonString(apiErrorResponse), 400)));

        ApiErrorResponse result = assertThrows(ResponseErrorException.class, () -> client.addChat(1L))
            .getApiErrorResponse();

        assertThat(result)
            .isNotNull()
            .isEqualTo(apiErrorResponse);
    }*/

    public static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
