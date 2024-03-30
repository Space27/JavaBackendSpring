package edu.java.scrapper.service.linkUpdateService.linkUpdateSender;

import edu.java.scrapper.service.client.botClient.request.LinkUpdateRequest;

public interface LinkUpdateSender {

    void send(LinkUpdateRequest linkUpdateRequest);
}
