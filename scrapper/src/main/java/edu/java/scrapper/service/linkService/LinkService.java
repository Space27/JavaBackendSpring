package edu.java.scrapper.service.linkService;

import edu.java.scrapper.controller.linksApi.exception.ChatNotExistsException;
import edu.java.scrapper.controller.linksApi.exception.LinkAlreadyExistsException;
import edu.java.scrapper.controller.linksApi.exception.LinkNotFoundException;
import edu.java.scrapper.domain.link.Link;
import java.net.URI;
import java.util.Collection;

public interface LinkService {

    Link add(Long chatId, URI url) throws ChatNotExistsException, LinkAlreadyExistsException;

    Link remove(Long chatId, URI url) throws ChatNotExistsException, LinkNotFoundException;

    Collection<Link> listAll(Long chatId) throws ChatNotExistsException;
}
