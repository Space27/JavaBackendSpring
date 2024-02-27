package edu.java.bot.service.clients.ScrapperClient;

import edu.java.bot.service.clients.ScrapperClient.schemas.LinkResponse;
import edu.java.bot.service.clients.ScrapperClient.schemas.ListLinkResponse;
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
    LinkResponse addLink(@RequestHeader("Tg-Chat-Id") Long id, @RequestBody URI link);

    @DeleteExchange("/links")
    LinkResponse removeLink(@RequestHeader("Tg-Chat-Id") Long id, @RequestBody URI link);
}
