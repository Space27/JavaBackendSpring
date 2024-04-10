package edu.java.scrapper.service.daoService;

import edu.java.scrapper.controller.chatApi.exception.ChatAlreadyExistsException;
import edu.java.scrapper.controller.chatApi.exception.ChatNotFoundException;

public interface TgChatService {

    void register(Long chatId) throws ChatAlreadyExistsException;

    void unregister(Long chatId) throws ChatNotFoundException;
}
