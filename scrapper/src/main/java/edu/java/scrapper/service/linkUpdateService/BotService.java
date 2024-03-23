package edu.java.scrapper.service.linkUpdateService;

import edu.java.scrapper.domain.dto.Link;
import edu.java.scrapper.service.client.botClient.BotClient;
import edu.java.scrapper.service.client.botClient.ResponseErrorException;
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

    private final BotClient botClient;

    public void sendMessagesByUpdateTime(
        Link link,
        Map<Long, OffsetDateTime> chats,
        Map<String, OffsetDateTime> messages
    ) {
        Map<String, List<Long>> groupedMessages = MessageDispatcherUtils.groupMessagesForChats(messages, chats);

        for (var group : groupedMessages.entrySet()) {
            try {
                botClient.updateLink(link.id(), link.url(), group.getKey(), group.getValue());
            } catch (ResponseErrorException e) {
                log.error("Bot Client error while link updating", e);
            } catch (Exception e) {
                log.error("Unhandled Bot Client exception while link updating", e);
            }
        }
    }
}
