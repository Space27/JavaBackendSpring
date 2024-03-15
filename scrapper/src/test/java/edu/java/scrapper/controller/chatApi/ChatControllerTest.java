package edu.java.scrapper.controller.chatApi;

import edu.java.scrapper.controller.chatApi.exception.ChatAlreadyExistsException;
import edu.java.scrapper.controller.chatApi.exception.ChatNotFoundException;
import edu.java.scrapper.service.daoService.TgChatService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ChatController.class)
class ChatControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean(name = "jooqTgChatService")
    TgChatService chatService;

    @Test
    @DisplayName("Корректный запрос регистрации")
    void post_shouldReturnOkForCorrectRequest() throws Exception {
        mockMvc.perform(post("/tg-chat/1")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Повторная регистрация")
    void post_shouldReturnErrorForRepeatRequest() throws Exception {
        Mockito.doThrow(new ChatAlreadyExistsException(0L)).when(chatService).register(any());

        mockMvc.perform(post("/tg-chat/1")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isConflict())
            .andExpect(MockMvcResultMatchers.jsonPath("$.code").exists())
            .andExpect(MockMvcResultMatchers.jsonPath("$.code").value("409"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.description").exists())
            .andExpect(MockMvcResultMatchers.jsonPath("$.description").value("Чат уже зарегистрирован"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.stacktrace").exists())
            .andExpect(MockMvcResultMatchers.jsonPath("$.exceptionMessage").exists())
            .andExpect(MockMvcResultMatchers.jsonPath("$.exceptionName").exists());
    }

    @Test
    @DisplayName("Корректный запрос удаления чата")
    void delete_shouldReturnOkForCorrectRequest() throws Exception {
        mockMvc.perform(delete("/tg-chat/1")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Удаление несуществующего чата")
    void delete_shouldReturnErrorForNotExistChat() throws Exception {
        Mockito.doThrow(new ChatNotFoundException(0L)).when(chatService).unregister(any());

        mockMvc.perform(delete("/tg-chat/1")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound())
            .andExpect(MockMvcResultMatchers.jsonPath("$.code").exists())
            .andExpect(MockMvcResultMatchers.jsonPath("$.code").value("404"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.description").exists())
            .andExpect(MockMvcResultMatchers.jsonPath("$.description").value("Чат не существует"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.stacktrace").exists())
            .andExpect(MockMvcResultMatchers.jsonPath("$.exceptionMessage").exists())
            .andExpect(MockMvcResultMatchers.jsonPath("$.exceptionName").exists());
    }

    @ParameterizedTest
    @ValueSource(strings = {"0", "-1", "a"})
    @DisplayName("Некорректный запрос удаления чата")
    void delete_shouldReturnErrorForIncorrectRequest(String id) throws Exception {
        mockMvc.perform(delete("/tg-chat/" + id)
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

    @ParameterizedTest
    @ValueSource(strings = {"0", "-1", "a"})
    @DisplayName("Некорректный запрос добавления чата")
    void post_shouldReturnErrorForIncorrectRequest(String id) throws Exception {
        mockMvc.perform(delete("/tg-chat/" + id)
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
}
