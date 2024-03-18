package edu.java.scrapper.service.daoService.jpa;

import edu.java.scrapper.controller.chatApi.exception.ChatAlreadyExistsException;
import edu.java.scrapper.controller.chatApi.exception.ChatNotFoundException;
import edu.java.scrapper.domain.dao.jpa.JpaChatLinkDao;
import edu.java.scrapper.domain.dao.jpa.JpaTgChatDao;
import edu.java.scrapper.domain.dao.jpa.entity.ChatEntity;
import edu.java.scrapper.service.daoService.TgChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
public class JpaTgChatService implements TgChatService {

    private final JpaTgChatDao chatDao;
    private final JpaChatLinkDao chatLinkDao;

    @Override
    @Transactional
    public void register(Long chatId) throws ChatAlreadyExistsException {
        if (chatDao.existsById(chatId)) {
            throw new ChatAlreadyExistsException(chatId);
        } else {
            ChatEntity chat = new ChatEntity(chatId);
            chatDao.save(chat);
        }
    }

    @Override
    @Transactional
    public void unregister(Long chatId) throws ChatNotFoundException {
        if (!chatDao.existsById(chatId)) {
            throw new ChatNotFoundException(chatId);
        } else {
            chatLinkDao.deleteAllByChatId(chatId);
            chatDao.deleteById(chatId);
        }
    }
}
