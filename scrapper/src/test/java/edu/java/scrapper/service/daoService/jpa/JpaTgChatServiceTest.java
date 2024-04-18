package edu.java.scrapper.service.daoService.jpa;

import edu.java.scrapper.controller.chatApi.exception.ChatAlreadyExistsException;
import edu.java.scrapper.controller.chatApi.exception.ChatNotFoundException;
import edu.java.scrapper.domain.dao.jpa.JpaChatLinkDao;
import edu.java.scrapper.domain.dao.jpa.JpaTgChatDao;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;

public class JpaTgChatServiceTest {

    JpaTgChatService chatService;
    JpaTgChatDao chatDao;

    @BeforeEach
    void init() {
        chatDao = Mockito.mock(JpaTgChatDao.class);
        chatService = new JpaTgChatService(chatDao, Mockito.mock(JpaChatLinkDao.class));
    }

    @Test
    @DisplayName("Создание нового чата")
    void register_shouldNoThrowExceptionsForRegisterNewChat() {
        assertDoesNotThrow(() -> chatService.register(0L));
    }

    @Test
    @DisplayName("Создание существующего чата")
    void register_shouldThrowExceptionsForRegisterOldChat() {
        Mockito.when(chatDao.existsById(any())).thenReturn(true);

        assertThrows(ChatAlreadyExistsException.class, () -> chatService.register(0L));
    }

    @Test
    @DisplayName("Удаление существующего чата")
    void unregister_shouldNoThrowExceptionsForUnregisterOldChat() {
        Mockito.when(chatDao.existsById(any())).thenReturn(true);

        assertDoesNotThrow(() -> chatService.unregister(0L));
    }

    @Test
    @DisplayName("Удаление несуществующего чата")
    void unregister_shouldThrowExceptionsForUnregisterNotExistingChat() {
        assertThrows(ChatNotFoundException.class, () -> chatService.unregister(0L));
    }
}
