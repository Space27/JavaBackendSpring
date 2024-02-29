package edu.java.scrapper.service.api.controller.chatApi;

import edu.java.scrapper.repository.LinkStorage;
import edu.java.scrapper.service.api.controller.exception.ChatAlreadyExistsException;
import edu.java.scrapper.service.api.controller.exception.ChatNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChatHandler {

    private final LinkStorage linkStorage;

    void addChat(Long id) throws ChatAlreadyExistsException {
        if (!linkStorage.contains(id)) {
            linkStorage.addChat(id);
        } else {
            throw new ChatAlreadyExistsException(id);
        }
    }

    void deleteChat(Long id) throws ChatNotFoundException {
        if (linkStorage.contains(id)) {
            linkStorage.deleteChat(id);
        } else {
            throw new ChatNotFoundException(id);
        }
    }
}
