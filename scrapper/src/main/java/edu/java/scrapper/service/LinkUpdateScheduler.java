package edu.java.scrapper.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class LinkUpdateScheduler {

    @Scheduled(fixedDelayString = "#{@scheduler.interval()}")
    public void update() {
        log.info("Update is done");
    }
}
