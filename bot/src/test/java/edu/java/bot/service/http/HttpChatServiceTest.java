package edu.java.bot.service.http;

import edu.java.bot.service.client.scrapper.ResponseErrorException;
import edu.java.bot.service.client.scrapper.ScrapperClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import static org.assertj.core.api.Assertions.assertThat;

class HttpChatServiceTest {

    HttpChatService chatService;
    ScrapperClient scrapperClient;

    @BeforeEach
    void init() {
        scrapperClient = Mockito.mock(ScrapperClient.class);
        chatService = new HttpChatService(scrapperClient);
    }

    @Test
    @DisplayName("Корректное добавление чата")
    void register_shouldReturnTrueForNewChat() {
        Long chatId = 1L;

        boolean result = chatService.register(chatId);

        assertThat(result)
            .isTrue();
    }

    @Test
    @DisplayName("Некорректное добавление чата")
    void register_shouldReturnFalseForOldChat() {
        Long chatId = 1L;
        Mockito.doThrow(ResponseErrorException.class).when(scrapperClient).addChat(chatId);

        boolean result = chatService.register(chatId);

        assertThat(result)
            .isFalse();
    }

    @Test
    @DisplayName("Корректное удаление чата")
    void unregister_shouldReturnTrueForOldChat() {
        Long chatId = 1L;

        boolean result = chatService.unregister(chatId);

        assertThat(result)
            .isTrue();
    }

    @Test
    @DisplayName("Некорректное удаление чата")
    void unregister_shouldReturnFalseForNewChat() {
        Long chatId = 1L;
        Mockito.doThrow(ResponseErrorException.class).when(scrapperClient).deleteChat(chatId);

        boolean result = chatService.unregister(chatId);

        assertThat(result)
            .isFalse();
    }
}
