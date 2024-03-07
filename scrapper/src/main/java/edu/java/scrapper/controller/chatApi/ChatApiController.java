package edu.java.scrapper.controller.chatApi;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/tg-chat")
@RequiredArgsConstructor
public class ChatApiController implements ChatApi {

    private final ChatHandler chatHandler;

    @Override
    public ResponseEntity<Void> addChat(Long id) {
        chatHandler.addChat(id);

        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<Void> deleteChat(Long id) {
        chatHandler.deleteChat(id);

        return ResponseEntity.ok().build();
    }
}
