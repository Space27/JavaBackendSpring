package edu.java.scrapper.service.clients.BotClient;

import edu.java.scrapper.service.clients.BotClient.schemas.LinkUpdate;
import java.net.URI;
import java.util.List;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.service.annotation.PostExchange;

public interface BotClient {

    @PostExchange("/updates")
    void updateLink(@RequestBody LinkUpdate linkUpdate);

    default void updateLink(Long id, URI url, String description, List<Long> tgChatIds) {
        updateLink(new LinkUpdate(id, url, description, tgChatIds));
    }
}
