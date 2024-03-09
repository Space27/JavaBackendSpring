package edu.java.scrapper.controller.linksApi;

import edu.java.scrapper.controller.linksApi.exception.ChatNotExistsException;
import edu.java.scrapper.controller.linksApi.exception.LinkAlreadyExistsException;
import edu.java.scrapper.controller.linksApi.exception.LinkNotFoundException;
import edu.java.scrapper.controller.response.LinkResponse;
import edu.java.scrapper.controller.response.ListLinkResponse;
import edu.java.scrapper.repository.LinkRepository;
import java.net.URI;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LinksHandler {

    private final LinkRepository linkRepository;

    ListLinkResponse getLinks(Long id) throws ChatNotExistsException {
        if (!linkRepository.contains(id)) {
            throw new ChatNotExistsException(id);
        }

        List<LinkRepository.Link> links = linkRepository.get(id);

        List<LinkResponse> linkResponses = links.stream()
            .map(link -> new LinkResponse(link.id(), URI.create(link.link())))
            .toList();

        return new ListLinkResponse(linkResponses, linkResponses.size());
    }

    LinkResponse addLink(Long id, URI link) throws ChatNotExistsException, LinkAlreadyExistsException {
        if (!linkRepository.contains(id)) {
            throw new ChatNotExistsException(id);
        }

        Long linkID = linkRepository.add(id, link.toString());
        if (linkID == null) {
            throw new LinkAlreadyExistsException(link);
        }

        return new LinkResponse(linkID, link);
    }

    LinkResponse removeLink(Long id, URI link) throws ChatNotExistsException, LinkNotFoundException {
        if (!linkRepository.contains(id)) {
            throw new ChatNotExistsException(id);
        }

        Long linkID = linkRepository.remove(id, link.toString());
        if (linkID == null) {
            throw new LinkNotFoundException(link);
        }

        return new LinkResponse(linkID, link);
    }
}
