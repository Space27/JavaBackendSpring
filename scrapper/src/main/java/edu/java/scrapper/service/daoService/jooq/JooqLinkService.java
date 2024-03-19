package edu.java.scrapper.service.daoService.jooq;

import edu.java.scrapper.controller.linksApi.exception.ChatNotExistsException;
import edu.java.scrapper.controller.linksApi.exception.LinkAlreadyExistsException;
import edu.java.scrapper.controller.linksApi.exception.LinkNotFoundException;
import edu.java.scrapper.domain.dao.jooq.JooqChatLinkDao;
import edu.java.scrapper.domain.dao.jooq.JooqLinkDao;
import edu.java.scrapper.domain.dao.jooq.JooqTgChatDao;
import edu.java.scrapper.domain.dto.Chat;
import edu.java.scrapper.domain.dto.Link;
import edu.java.scrapper.service.daoService.LinkService;
import java.net.URI;
import java.util.Collection;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
public class JooqLinkService implements LinkService {

    private final JooqLinkDao linkDao;
    private final JooqTgChatDao chatDao;
    private final JooqChatLinkDao chatLinkDao;

    @Override
    @Transactional
    public Link add(Long chatId, URI url) throws ChatNotExistsException, LinkAlreadyExistsException {
        chatExists(chatId);

        Link link = linkDao.add(url);

        if (!chatLinkDao.add(chatId, link.id())) {
            throw new LinkAlreadyExistsException(url);
        }

        return link;
    }

    @Override
    @Transactional
    public Link remove(Long chatId, URI url) throws ChatNotExistsException, LinkNotFoundException {
        chatExists(chatId);

        Link link = linkDao.find(url);

        if (link == null || !chatLinkDao.remove(chatId, link.id())) {
            throw new LinkNotFoundException(url);
        }

        return link;
    }

    @Override
    @Transactional
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
