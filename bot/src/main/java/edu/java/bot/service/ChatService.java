package edu.java.bot.service;

import edu.java.bot.service.client.scrapperClient.ResponseErrorException;
import edu.java.bot.service.client.scrapperClient.ScrapperClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final ScrapperClient scrapperClient;

    boolean register(Long chatId) {
        try {
            scrapperClient.addChat(chatId);
            return true;
        } catch (ResponseErrorException e) {
            return false;
        }
    }

    boolean unregister(Long chatId) {
        try {
            scrapperClient.deleteChat(chatId);
            return true;
        } catch (ResponseErrorException e) {
            return false;
        }
    }
}
