package edu.java.scrapper.service.linkUpdateService.clientUpdate;

import java.net.URI;
import java.time.OffsetDateTime;
import java.util.Map;

public interface ClientUpdateService {

    boolean supports(URI link);

    Map<String, OffsetDateTime> handle(URI link, OffsetDateTime lastCheckTime);
}
