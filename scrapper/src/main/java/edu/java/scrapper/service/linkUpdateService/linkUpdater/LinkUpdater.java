package edu.java.scrapper.service.linkUpdateService.linkUpdater;

import java.time.Duration;

public interface LinkUpdater {

    Duration MIN_INTERVAL = Duration.ofMinutes(10L);

    int update();
}
