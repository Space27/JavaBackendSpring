package edu.java.scrapper.service.linkUpdateService.linkUpdater;

import edu.java.scrapper.domain.dao.jpa.JpaChatLinkDao;
import edu.java.scrapper.domain.dao.jpa.JpaLinkDao;
import edu.java.scrapper.domain.dao.jpa.entity.ChatLinkEntity;
import edu.java.scrapper.domain.dao.jpa.entity.LinkEntity;
import edu.java.scrapper.domain.dto.Link;
import edu.java.scrapper.service.linkUpdateService.BotService;
import edu.java.scrapper.service.linkUpdateService.clientUpdate.ClientUpdater;
import java.net.URI;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
public class JpaLinkUpdater implements LinkUpdater {

    private final ClientUpdater clientUpdater;
    private final BotService botService;
    private final JpaLinkDao linkDao;
    private final JpaChatLinkDao chatLinkDao;

    @Override
    @Transactional
    public int update() {
        OffsetDateTime checkTime = OffsetDateTime.now().withNano(0);
        int updatedLinksAmount = 0;
        List<LinkEntity> linksToCheck =
            linkDao.findLinkEntitiesByLastCheckAtLessThanEqual(checkTime.minus(MIN_INTERVAL));

        for (LinkEntity link : linksToCheck) {
            Map<String, OffsetDateTime> descriptions = clientUpdater.handle(URI.create(link.getUrl()), checkTime);

            if (!descriptions.isEmpty()) {
                ++updatedLinksAmount;

                Map<Long, OffsetDateTime> chats = chatLinkDao.findAllByLink(link).stream()
                    .collect(Collectors.toMap(
                        chatLinkEntity -> chatLinkEntity.getChat().getId(),
                        ChatLinkEntity::getCreatedAt
                    ));

                botService.sendMessagesByUpdateTime(dtoMapper(link), chats, descriptions);
            }

            link.setLastCheckAt(checkTime);
            linkDao.save(link);
        }

        return updatedLinksAmount;
    }

    private Link dtoMapper(LinkEntity link) {
        return new Link(
            link.getId(),
            URI.create(link.getUrl()),
            link.getLastCheckAt(),
            link.getCreatedAt()
        );
    }
}
