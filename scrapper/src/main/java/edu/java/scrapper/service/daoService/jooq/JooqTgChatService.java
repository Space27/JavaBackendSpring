package edu.java.scrapper.service.daoService.jooq;

import edu.java.scrapper.controller.chatApi.exception.ChatAlreadyExistsException;
import edu.java.scrapper.controller.chatApi.exception.ChatNotFoundException;
import edu.java.scrapper.domain.dao.jooq.JooqTgChatDao;
import edu.java.scrapper.service.daoService.TgChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class JooqTgChatService implements TgChatService {

    private final JooqTgChatDao tgChatDao;

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
