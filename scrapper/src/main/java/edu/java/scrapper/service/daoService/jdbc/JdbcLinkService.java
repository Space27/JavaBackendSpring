package edu.java.scrapper.service.daoService.jdbc;

import edu.java.scrapper.controller.linksApi.exception.ChatNotExistsException;
import edu.java.scrapper.controller.linksApi.exception.LinkAlreadyExistsException;
import edu.java.scrapper.controller.linksApi.exception.LinkNotFoundException;
import edu.java.scrapper.domain.dao.jdbc.JdbcChatLinkDao;
import edu.java.scrapper.domain.dao.jdbc.JdbcLinkDao;
import edu.java.scrapper.domain.dao.jdbc.JdbcTgChatDao;
import edu.java.scrapper.domain.dto.Chat;
import edu.java.scrapper.domain.dto.Link;
import edu.java.scrapper.service.daoService.LinkService;
import java.net.URI;
import java.util.Collection;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class JdbcLinkService implements LinkService {

    private final JdbcLinkDao linkDao;
    private final JdbcTgChatDao chatDao;
    private final JdbcChatLinkDao chatLinkDao;

    @Override
    public Link add(Long chatId, URI url) throws ChatNotExistsException, LinkAlreadyExistsException {
        chatExists(chatId);

        Link link = linkDao.add(url);

        if (!chatLinkDao.add(chatId, link.id())) {
            throw new LinkAlreadyExistsException(url);
        }

        return link;
    }

    @Override
    public Link remove(Long chatId, URI url) throws ChatNotExistsException, LinkNotFoundException {
        chatExists(chatId);

        Link link = linkDao.find(url);

        if (link == null || !chatLinkDao.remove(chatId, link.id())) {
            throw new LinkNotFoundException(url);
        }

        return link;
    }

    @Override
    public Collection<Link> listAll(Long chatId) throws ChatNotExistsException {
        chatExists(chatId);

        return chatLinkDao.findLinksByChat(chatId);
    }

    private void chatExists(Long chatId) throws ChatNotExistsException {
        Chat chat = chatDao.findById(chatId);

        if (chat == null) {
            throw new ChatNotExistsException(chatId);
        }
    }
}
