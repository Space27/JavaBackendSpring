package edu.java.bot.service.client.scrapper;

import edu.java.bot.service.client.scrapper.request.AddLinkRequest;
import edu.java.bot.service.client.scrapper.request.RemoveLinkRequest;
import edu.java.bot.service.client.scrapper.response.LinkResponse;
import edu.java.bot.service.client.scrapper.response.ListLinkResponse;
import java.net.URI;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.service.annotation.DeleteExchange;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.PostExchange;

public interface ScrapperClient {

    @PostExchange("/tg-chat/{id}")
    void addChat(@PathVariable Long id);

    @DeleteExchange("/tg-chat/{id}")
    void deleteChat(@PathVariable Long id);

    @GetExchange("/links")
    ListLinkResponse getLinks(@RequestHeader("Tg-Chat-Id") Long id);

    @PostExchange("/links")
    LinkResponse addLink(@RequestHeader("Tg-Chat-Id") Long id, @RequestBody AddLinkRequest addLinkRequest);

    @DeleteExchange("/links")
    LinkResponse removeLink(@RequestHeader("Tg-Chat-Id") Long id, @RequestBody RemoveLinkRequest removeLinkRequest);

    default LinkResponse removeLink(Long id, URI link) {
        return removeLink(id, new RemoveLinkRequest(link));
    }

    default LinkResponse addLink(Long id, URI link) {
        return addLink(id, new AddLinkRequest(link));
    }
}
