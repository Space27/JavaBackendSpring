package edu.java.scrapper.service.tgChatService;

import edu.java.scrapper.controller.chatApi.exception.ChatAlreadyExistsException;
import edu.java.scrapper.controller.chatApi.exception.ChatNotFoundException;
import edu.java.scrapper.domain.tgChat.jdbcImpl.JdbcTgChatDao;
import edu.java.scrapper.service.tgChatService.jdbc.JdbcTgChatService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;

class JdbcTgChatServiceTest {

    JdbcTgChatService chatService;
    JdbcTgChatDao chatDao;

    @BeforeEach
    void init() {
        chatDao = Mockito.mock(JdbcTgChatDao.class);
        chatService = new JdbcTgChatService(chatDao);
    }

    @Test
    @DisplayName("Создание нового чата")
    void register_shouldNoThrowExceptionsForRegisterNewChat() {
        Mockito.when(chatDao.add(any())).thenReturn(true);

        assertDoesNotThrow(() -> chatService.register(0L));
    }

    @Test
    @DisplayName("Создание существующего чата")
    void register_shouldThrowExceptionsForRegisterOldChat() {
        assertThrows(ChatAlreadyExistsException.class, () -> chatService.register(0L));
    }

    @Test
    @DisplayName("Удаление существующего чата")
    void unregister_shouldNoThrowExceptionsForUnregisterOldChat() {
        Mockito.when(chatDao.remove(any())).thenReturn(true);

        assertDoesNotThrow(() -> chatService.unregister(0L));
    }

    @Test
    @DisplayName("Удаление несуществующего чата")
    void unregister_shouldThrowExceptionsForUnregisterNotExistingChat() {
        assertThrows(ChatNotFoundException.class, () -> chatService.unregister(0L));
    }
}
