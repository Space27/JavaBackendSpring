package edu.java.scrapper.service.linkUpdateService.clientUpdate;

import edu.java.scrapper.service.client.gitHubClient.GitHubClient;
import edu.java.scrapper.service.client.gitHubClient.RepositoryResponse;
import java.net.URI;
import java.time.OffsetDateTime;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@Service
@RequiredArgsConstructor
public class GitHubClientUpdateService implements ClientUpdateService {

    private static final Pattern REGEX = Pattern.compile("github\\.com/(\\w+)/(\\w+)");
    private static final String UPDATE_ANSWER = "Github: Последнее обновление в %s\n";

    private final GitHubClient client;

    @Override
    public boolean supports(URI link) {
        Matcher matcher = REGEX.matcher(link.toString());

        if (matcher.find()) {
            String owner = matcher.group(1);
            String repo = matcher.group(2);

            try {
                client.fetchRepository(owner, repo);
                return true;
            } catch (WebClientResponseException e) {
                return false;
            }
        }

        return false;
    }

    @Override
    public Map<String, OffsetDateTime> handle(URI link, OffsetDateTime lastCheckTime) {
        Matcher matcher = REGEX.matcher(link.toString());

        if (matcher.find()) {
            String owner = matcher.group(1);
            String repo = matcher.group(2);

            RepositoryResponse response = client.fetchRepository(owner, repo);
            OffsetDateTime lastModified = response.update();

            if (lastModified.isAfter(lastCheckTime)) {
                return Map.of(String.format(UPDATE_ANSWER, lastModified), lastModified);
            }
        }

        return Map.of();
    }
}
