package edu.java.scrapper.service.linkUpdateService.linkUpdateSender;

import edu.java.scrapper.service.client.botClient.BotClient;
import edu.java.scrapper.service.client.botClient.ResponseErrorException;
import edu.java.scrapper.service.client.botClient.request.LinkUpdateRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
public class BotClientSender implements LinkUpdateSender {

    private final BotClient botClient;

    @Override
    public void send(LinkUpdateRequest linkUpdateRequest) {
        try {
            botClient.updateLink(linkUpdateRequest);
        } catch (ResponseErrorException e) {
            log.error("Bot Client error while link updating", e);
        } catch (Exception e) {
            log.error("Unhandled Bot Client exception while link updating", e);
        }
    }
}
