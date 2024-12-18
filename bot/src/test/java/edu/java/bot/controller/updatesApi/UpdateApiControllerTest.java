package edu.java.bot.controller.updatesApi;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.java.bot.controller.request.LinkUpdateRequest;
import edu.java.bot.service.RateLimitService;
import java.net.URI;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UpdatesApiController.class)
@Import({RateLimitService.class})
class UpdateApiControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    UpdateHandler updateHandler;

    @Test
    @DisplayName("Корректный запрос")
    void post_shouldReturnOkForCorrectRequest() throws Exception {
        LinkUpdateRequest
            linkUpdateRequest = new LinkUpdateRequest(1L, URI.create("https://gist.github.com/"), "des", List.of(1L));

        mockMvc.perform(post("/updates")
                .content(asJsonString(linkUpdateRequest))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());
    }

    @ParameterizedTest
    @MethodSource("provideIncorrectLinkUpdates")
    @DisplayName("Некорректные запросы")
    void post_shouldReturnApiErrorResponseForIncorrectRequest(LinkUpdateRequest linkUpdateRequest) throws Exception {
        mockMvc.perform(post("/updates")
                .content(asJsonString(linkUpdateRequest))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest())
            .andExpect(MockMvcResultMatchers.jsonPath("$.code").exists())
            .andExpect(MockMvcResultMatchers.jsonPath("$.code").value("400"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.description").exists())
            .andExpect(MockMvcResultMatchers.jsonPath("$.description").value("Некорректные параметры запроса"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.stacktrace").exists())
            .andExpect(MockMvcResultMatchers.jsonPath("$.exceptionMessage").exists())
            .andExpect(MockMvcResultMatchers.jsonPath("$.exceptionName").exists());
    }

    @Test
    @DisplayName("Превышение допустимого числа запросов")
    void post_shouldReturnTooManyRequestsForSeveralRequests() throws Exception {
        LinkUpdateRequest
            linkUpdateRequest = new LinkUpdateRequest(1L, URI.create("https://gist.github.com/"), "des", List.of(1L));

        for (int i = 0; i < 20; ++i) {
            mockMvc.perform(post("/updates")
                .remoteAddress("139.56.211.186")
                .content(asJsonString(linkUpdateRequest))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));
        }

        mockMvc.perform(post("/updates")
                .remoteAddress("139.56.211.186")
                .content(asJsonString(linkUpdateRequest))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isTooManyRequests());
    }

    @Test
    @DisplayName("Превышение допустимого числа запросов для другого IP")
    void post_shouldReturnOkForOtherIP() throws Exception {
        LinkUpdateRequest
            linkUpdateRequest = new LinkUpdateRequest(1L, URI.create("https://gist.github.com/"), "des", List.of(1L));

        for (int i = 0; i < 20; ++i) {
            mockMvc.perform(post("/updates")
                .remoteAddress("139.56.211.186")
                .content(asJsonString(linkUpdateRequest))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));
        }

        mockMvc.perform(post("/updates")
                .remoteAddress("130.159.167.67")
                .content(asJsonString(linkUpdateRequest))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());
    }

    private static Stream<Arguments> provideIncorrectLinkUpdates() {
        return Stream.of(
            Arguments.of(new LinkUpdateRequest(null, URI.create("https://gist.github.com/"), "des", List.of(1L))),
            Arguments.of(new LinkUpdateRequest(1L, null, "des", List.of(1L))),
            Arguments.of(new LinkUpdateRequest(1L, URI.create("https://gist.github.com/"), null, List.of(1L))),
            Arguments.of(new LinkUpdateRequest(1L, URI.create("https://gist.github.com/"), "des", null)),
            Arguments.of(new LinkUpdateRequest(1L, URI.create("https://gist.github.com/"), "des", List.of(0L))),
            Arguments.of(new LinkUpdateRequest(1L, URI.create("https://gist.github.com/"), "", List.of(1L))),
            Arguments.of(new LinkUpdateRequest(
                1L,
                URI.create("https://gist.github.com/"),
                "des",
                Collections.emptyList()
            ))
        );
    }

    public static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
