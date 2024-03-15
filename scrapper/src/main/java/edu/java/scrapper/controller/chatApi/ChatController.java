package edu.java.scrapper.controller.chatApi;

import edu.java.scrapper.service.daoService.TgChatService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/tg-chat")
public class ChatController implements ChatApi {

    private final TgChatService chatService;

    public ChatController(@Qualifier("jooqTgChatService") TgChatService chatService) {
        this.chatService = chatService;
    }

    @Override
    public void addChat(Long id) {
        chatService.register(id);
    }

    @Override
    public void deleteChat(Long id) {
        chatService.unregister(id);
    }
}
