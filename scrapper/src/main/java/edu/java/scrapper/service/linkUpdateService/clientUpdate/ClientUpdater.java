package edu.java.scrapper.service.linkUpdateService.clientUpdate;

import java.net.URI;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ClientUpdater {

    private final List<ClientUpdateService> clientUpdaters;

    public boolean supports(URI link) {
        return clientUpdaters.stream()
            .anyMatch(clientUpdateService -> clientUpdateService.supports(link));
    }

    public String handle(URI link, OffsetDateTime lastCheckTime) {
        return clientUpdaters.stream()
            .map(clientUpdateService -> clientUpdateService.handle(link, lastCheckTime))
            .filter(Objects::nonNull)
            .collect(Collectors.joining());
    }
}
