package edu.java.scrapper.controller.chatApi;

import edu.java.scrapper.controller.chatApi.exception.ChatAlreadyExistsException;
import edu.java.scrapper.controller.chatApi.exception.ChatNotFoundException;
import edu.java.scrapper.repository.LinkRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChatHandler {

    private final LinkRepository linkRepository;

    void addChat(Long id) throws ChatAlreadyExistsException {
        if (!linkRepository.contains(id)) {
            linkRepository.addChat(id);
        } else {
            throw new ChatAlreadyExistsException(id);
        }
    }

    void deleteChat(Long id) throws ChatNotFoundException {
        if (linkRepository.contains(id)) {
            linkRepository.deleteChat(id);
        } else {
            throw new ChatNotFoundException(id);
        }
    }
}
