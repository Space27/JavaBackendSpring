package edu.java.scrapper.service.daoService.jpa;

import edu.java.scrapper.controller.linksApi.exception.ChatNotExistsException;
import edu.java.scrapper.controller.linksApi.exception.LinkAlreadyExistsException;
import edu.java.scrapper.controller.linksApi.exception.LinkNotFoundException;
import edu.java.scrapper.domain.dao.jpa.JpaChatLinkDao;
import edu.java.scrapper.domain.dao.jpa.JpaLinkDao;
import edu.java.scrapper.domain.dao.jpa.JpaTgChatDao;
import edu.java.scrapper.domain.dao.jpa.entity.ChatEntity;
import edu.java.scrapper.domain.dao.jpa.entity.ChatLinkEntity;
import edu.java.scrapper.domain.dao.jpa.entity.LinkEntity;
import edu.java.scrapper.domain.dto.Link;
import edu.java.scrapper.service.daoService.LinkService;
import java.net.URI;
import java.util.Collection;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
public class JpaLinkService implements LinkService {

    private final JpaChatLinkDao chatLinkDao;
    private final JpaLinkDao linkDao;
    private final JpaTgChatDao chatDao;

    @Override
    @Transactional
    public Link add(Long chatId, URI url) throws ChatNotExistsException, LinkAlreadyExistsException {
        ChatEntity chat = findChat(chatId);
        Optional<LinkEntity> linkEntityOptional = linkDao.findByUrl(url.toString());
        LinkEntity link;

        if (linkEntityOptional.isPresent()) {
            link = linkEntityOptional.get();
        } else {
            link = new LinkEntity(url);
            linkDao.save(link);
        }

        if (chatLinkDao.existsByChatAndLink(chat, link)) {
            throw new LinkAlreadyExistsException(url);
        } else {
            ChatLinkEntity chatLinkEntity = new ChatLinkEntity(chat, link);
            chatLinkDao.save(chatLinkEntity);
        }

        return dtoMapper(link);
    }

    @Override
    @Transactional
    public Link remove(Long chatId, URI url) throws ChatNotExistsException, LinkNotFoundException {
        ChatEntity chat = findChat(chatId);
        Optional<LinkEntity> linkEntityOptional = linkDao.findByUrl(url.toString());
        LinkEntity link;

        if (linkEntityOptional.isPresent()) {
            link = linkEntityOptional.get();
        } else {
            throw new LinkNotFoundException(url);
        }

        Long removedRows = chatLinkDao.deleteByChatAndLink(chat, link);
        if (removedRows == 0) {
            throw new LinkNotFoundException(url);
        }
        if (chatLinkDao.findAllByLink(link).isEmpty()) {
            linkDao.delete(link);
        }

        return dtoMapper(link);
    }

    @Override
    @Transactional
    public Collection<Link> listAll(Long chatId) throws ChatNotExistsException {
        ChatEntity chat = findChat(chatId);

        return chatLinkDao.findAllByChat(chat).stream()
            .map(chatLinkEntity -> dtoMapper(chatLinkEntity.getLink()))
            .toList();
    }

    private ChatEntity findChat(Long chatId) throws ChatNotExistsException {
        return chatDao.findById(chatId).orElseThrow(() -> new ChatNotExistsException(chatId));
    }

    private Link dtoMapper(LinkEntity link) {
        return new Link(
            link.getId(),
            URI.create(link.getUrl()),
            link.getLastCheckAt(),
            link.getCreatedAt()
        );
    }
}
