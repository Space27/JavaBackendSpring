package edu.java.scrapper.service.daoService.jdbc;

import edu.java.scrapper.controller.chatApi.exception.ChatAlreadyExistsException;
import edu.java.scrapper.controller.chatApi.exception.ChatNotFoundException;
import edu.java.scrapper.domain.dao.jdbc.JdbcTgChatDao;
import edu.java.scrapper.service.daoService.TgChatService;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class JdbcTgChatService implements TgChatService {

    private final JdbcTgChatDao tgChatDao;

    @Override
    public void register(Long chatId) {
        if (!tgChatDao.add(chatId)) {
            throw new ChatAlreadyExistsException(chatId);
        }
    }

    @Override
    public void unregister(Long chatId) {
        if (!tgChatDao.remove(chatId)) {
            throw new ChatNotFoundException(chatId);
        }
    }
}
