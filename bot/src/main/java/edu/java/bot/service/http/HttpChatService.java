package edu.java.bot.service.http;

import edu.java.bot.service.ChatService;
import edu.java.bot.service.client.scrapperClient.ResponseErrorException;
import edu.java.bot.service.client.scrapperClient.ScrapperClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class HttpChatService implements ChatService {

    private final ScrapperClient scrapperClient;

    @Override
    public boolean register(Long chatId) {
        try {
            scrapperClient.addChat(chatId);
            return true;
        } catch (ResponseErrorException e) {
            log.error("Exception while Scrapper Client chat adding\n" + e.getApiErrorResponse());
            return false;
        } catch (Exception e) {
            log.error("Unexpected exception while Scrapper Client chat adding", e);
            return false;
        }
    }

    @Override
    public boolean unregister(Long chatId) {
        try {
            scrapperClient.deleteChat(chatId);
            return true;
        } catch (ResponseErrorException e) {
            log.error("Exception while Scrapper Client chat deleting\n" + e.getApiErrorResponse());
            return false;
        } catch (Exception e) {
            log.error("Unexpected exception while Scrapper Client chat deleting", e);
            return false;
        }
    }
}
