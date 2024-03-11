package edu.java.scrapper.service.linkUpdateService.clientUpdate;

import java.net.URI;
import java.time.OffsetDateTime;

public interface ClientUpdateService {

    boolean supports(URI link);

    String handle(URI link, OffsetDateTime lastCheckTime);
}
