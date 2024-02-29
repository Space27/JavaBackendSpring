package edu.java.scrapper.service.api.controller.linksApi;

import edu.java.scrapper.repository.LinkStorage;
import edu.java.scrapper.service.api.controller.exception.ChatNotExistsException;
import edu.java.scrapper.service.api.controller.exception.LinkAlreadyExistsException;
import edu.java.scrapper.service.api.controller.exception.LinkNotFoundException;
import edu.java.scrapper.service.api.controller.response.LinkResponse;
import edu.java.scrapper.service.api.controller.response.ListLinkResponse;
import java.net.URI;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LinksHandler {

    private final LinkStorage linkStorage;

    ListLinkResponse getLinks(Long id) throws ChatNotExistsException {
        if (!linkStorage.contains(id)) {
            throw new ChatNotExistsException(id);
        }

        List<LinkStorage.Link> links = linkStorage.get(id);

        List<LinkResponse> linkResponses = links.stream()
            .map(link -> new LinkResponse(link.id(), URI.create(link.link())))
            .toList();

        return new ListLinkResponse(linkResponses, linkResponses.size());
    }

    LinkResponse addLink(Long id, URI link) throws ChatNotExistsException, LinkAlreadyExistsException {
        if (!linkStorage.contains(id)) {
            throw new ChatNotExistsException(id);
        }

        Long linkID = linkStorage.add(id, link.toString());
        if (linkID == null) {
            throw new LinkAlreadyExistsException(link);
        }

        return new LinkResponse(linkID, link);
    }

    LinkResponse removeLink(Long id, URI link) throws ChatNotExistsException, LinkNotFoundException {
        if (!linkStorage.contains(id)) {
            throw new ChatNotExistsException(id);
        }

        Long linkID = linkStorage.remove(id, link.toString());
        if (linkID == null) {
            throw new LinkNotFoundException(link);
        }

        return new LinkResponse(linkID, link);
    }
}
