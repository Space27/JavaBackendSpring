package edu.java.scrapper.controller.linksApi;

import edu.java.scrapper.controller.request.AddLinkRequest;
import edu.java.scrapper.controller.request.RemoveLinkRequest;
import edu.java.scrapper.controller.response.LinkResponse;
import edu.java.scrapper.controller.response.ListLinkResponse;
import edu.java.scrapper.domain.dto.Link;
import edu.java.scrapper.service.daoService.LinkService;
import java.util.List;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/links")
public class LinksController implements LinksApi {

    private final LinkService linkService;

    public LinksController(@Qualifier("jooqLinkService") LinkService linkService) {
        this.linkService = linkService;
    }

    @Override
    public ListLinkResponse getLinks(Long id) {
        List<LinkResponse> linkResponses = linkService.listAll(id).stream()
            .map(link -> new LinkResponse(link.id(), link.url()))
            .toList();

        return new ListLinkResponse(linkResponses, linkResponses.size());
    }

    @Override
    public LinkResponse addLink(Long id, AddLinkRequest addLinkRequest) {
        Link link = linkService.add(id, addLinkRequest.link());

        return new LinkResponse(link.id(), link.url());
    }

    @Override
    public LinkResponse deleteLink(Long id, RemoveLinkRequest removeLinkRequest) {
        Link link = linkService.remove(id, removeLinkRequest.link());

        return new LinkResponse(link.id(), link.url());
    }
}
