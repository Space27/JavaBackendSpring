package edu.java.scrapper.service.linkUpdateService.linkUpdater;

import edu.java.scrapper.domain.dao.jooq.JooqChatLinkDao;
import edu.java.scrapper.domain.dao.jooq.JooqLinkDao;
import edu.java.scrapper.domain.dto.ChatLink;
import edu.java.scrapper.domain.dto.Link;
import edu.java.scrapper.service.linkUpdateService.BotService;
import edu.java.scrapper.service.linkUpdateService.clientUpdate.ClientUpdater;
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
public class JooqLinkUpdater implements LinkUpdater {

    private final ClientUpdater clientUpdater;
    private final BotService botService;
    private final JooqLinkDao linkDao;
    private final JooqChatLinkDao chatLinkDao;

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

                botService.sendMessagesByUpdateTime(link, chats, descriptions);
            }

            linkDao.add(link.url(), checkTime);
        }

        return updatedLinksAmount;
    }
}