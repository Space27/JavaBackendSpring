package edu.java.scrapper.service.linkUpdateService;

import edu.java.scrapper.domain.dto.Link;
import edu.java.scrapper.service.client.botClient.request.LinkUpdateRequest;
import edu.java.scrapper.service.linkUpdateService.linkUpdateSender.LinkUpdateSender;
import edu.java.scrapper.util.MessageDispatcherUtils;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class BotService {

    private final LinkUpdateSender linkUpdateSender;

    public void sendMessagesByUpdateTime(
        Link link,
        Map<Long, OffsetDateTime> chats,
        Map<String, OffsetDateTime> messages
    ) {
        Map<String, List<Long>> groupedMessages = MessageDispatcherUtils.groupMessagesForChats(messages, chats);

        for (var group : groupedMessages.entrySet()) {
            LinkUpdateRequest linkUpdateRequest =
                new LinkUpdateRequest(link.id(), link.url(), group.getKey(), group.getValue());
            linkUpdateSender.send(linkUpdateRequest);
        }
    }
}
