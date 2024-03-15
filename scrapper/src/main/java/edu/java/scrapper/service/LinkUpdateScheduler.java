package edu.java.scrapper.service;

import edu.java.scrapper.service.linkUpdateService.linkUpdater.LinkUpdater;
import java.time.OffsetDateTime;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class LinkUpdateScheduler {

    private final LinkUpdater linkUpdater;

    public LinkUpdateScheduler(@Qualifier("jooqLinkUpdater") LinkUpdater linkUpdater) {
        this.linkUpdater = linkUpdater;
    }

    @Scheduled(fixedDelayString = "#{@scheduler.interval()}")
    public void update() {
        int updatedLinksCount = linkUpdater.update();

        log.info(String.format("In %s was updated %d links", OffsetDateTime.now(), updatedLinksCount));
    }
}
