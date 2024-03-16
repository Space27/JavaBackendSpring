package edu.java.scrapper.service.linkUpdateService.linkUpdater;

import edu.java.scrapper.domain.dao.jdbc.JdbcChatLinkDao;
import edu.java.scrapper.domain.dao.jdbc.JdbcLinkDao;
import edu.java.scrapper.domain.dto.ChatLink;
import edu.java.scrapper.domain.dto.Link;
import edu.java.scrapper.service.client.bot.BotClient;
import edu.java.scrapper.service.client.bot.ResponseErrorException;
import edu.java.scrapper.service.linkUpdateService.clientUpdate.ClientUpdater;
import edu.java.scrapper.util.MessageDispatcherUtils;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class JdbcLinkUpdater implements LinkUpdater {

    private static final Duration MIN_INTERVAL = Duration.ofMinutes(15L);

    private final ClientUpdater clientUpdater;
    private final BotClient botClient;
    private final JdbcLinkDao linkDao;
    private final JdbcChatLinkDao chatLinkDao;

    @Override
    public int update() {
        OffsetDateTime checkTime = OffsetDateTime.now().withNano(0);
        int updatedLinksAmount = 0;
        List<Link> linksToCheck = linkDao.findAll(checkTime.minus(MIN_INTERVAL));

        for (Link link : linksToCheck) {
            Map<String, OffsetDateTime> descriptions = clientUpdater.handle(link.url(), checkTime);

            if (!descriptions.isEmpty()) {
                ++updatedLinksAmount;

                Map<Long, OffsetDateTime> chats = chatLinkDao.findAllByLink(link.id()).stream()
                    .collect(Collectors.toMap(ChatLink::chatId, ChatLink::createdAt));

                Map<String, List<Long>> groupedMessages =
                    MessageDispatcherUtils.groupMessagesForChats(descriptions, chats);

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

            linkDao.add(link.url(), checkTime);
        }

        return updatedLinksAmount;
    }
}
