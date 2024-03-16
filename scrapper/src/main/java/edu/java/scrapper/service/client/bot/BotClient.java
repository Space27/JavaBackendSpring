package edu.java.scrapper.service.client.bot;

import edu.java.scrapper.service.client.bot.request.LinkUpdateRequest;
import java.net.URI;
import java.util.List;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.service.annotation.PostExchange;

public interface BotClient {

    @PostExchange("/updates")
    void updateLink(@RequestBody LinkUpdateRequest linkUpdateRequest);

    default void updateLink(Long id, URI url, String description, List<Long> tgChatIds) {
        updateLink(new LinkUpdateRequest(id, url, description, tgChatIds));
    }
}
