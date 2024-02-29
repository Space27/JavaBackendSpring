package edu.java.scrapper.service.api.controller.linksApi;

import edu.java.scrapper.service.api.controller.request.AddLinkRequest;
import edu.java.scrapper.service.api.controller.request.RemoveLinkRequest;
import edu.java.scrapper.service.api.controller.response.LinkResponse;
import edu.java.scrapper.service.api.controller.response.ListLinkResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/links")
@RequiredArgsConstructor
public class LinksApiController implements LinksApi {

    private final LinksHandler linksHandler;

    @Override
    public ResponseEntity<ListLinkResponse> getLinks(Long id) {
        ListLinkResponse listLinkResponse = linksHandler.getLinks(id);

        return ResponseEntity.ok(listLinkResponse);
    }

    @Override
    public ResponseEntity<LinkResponse> addLink(Long id, AddLinkRequest addLinkRequest) {
        LinkResponse linkResponse = linksHandler.addLink(id, addLinkRequest.link());

        return ResponseEntity.ok(linkResponse);
    }

    @Override
    public ResponseEntity<LinkResponse> deleteLink(Long id, RemoveLinkRequest removeLinkRequest) {
        LinkResponse linkResponse = linksHandler.removeLink(id, removeLinkRequest.link());

        return ResponseEntity.ok(linkResponse);
    }
}
