package edu.java.scrapper.service.linkUpdateService.linkUpdater;

import edu.java.scrapper.domain.dao.jdbc.JdbcChatLinkDao;
import edu.java.scrapper.domain.dao.jdbc.JdbcLinkDao;
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
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
public class JdbcLinkUpdater implements LinkUpdater {

    private final ClientUpdater clientUpdater;
    private final BotService botService;
    private final JdbcLinkDao linkDao;
    private final JdbcChatLinkDao chatLinkDao;

    @Override
    @Transactional
    public int update() {
        OffsetDateTime checkTime = OffsetDateTime.now().withNano(0);
        int updatedLinksAmount = 0;
        List<Link> linksToCheck = linkDao.findAll(checkTime.minus(MIN_INTERVAL));

        for (Link link : linksToCheck) {
            Map<String, OffsetDateTime> descriptions = clientUpdater.handle(link.url(), link.lastCheckAt());

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
