package edu.java.scrapper.service.linkUpdateService.clientUpdate.gitHubClientUpdate;

import edu.java.scrapper.service.client.gitHubClient.GitHubClient;
import edu.java.scrapper.service.client.gitHubClient.dto.IssueResponse;
import edu.java.scrapper.service.linkUpdateService.clientUpdate.ClientUpdateService;
import java.net.URI;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GitHubClientUpdateIssueService implements ClientUpdateService {

    private static final Pattern REGEX = Pattern.compile("github\\.com/([\\w-_.]+)/([\\w-_.]+)");
    private static final String NEW_ISSUE_ANSWER = "Github: Новый тикет с названием %s создан в %s";

    private final GitHubClient client;

    @Override
    public boolean supports(URI link) {
        Matcher matcher = REGEX.matcher(link.toString());

        if (matcher.find()) {
            String owner = matcher.group(1);
            String repo = matcher.group(2);

            try {
                client.fetchIssues(owner, repo);
                return true;
            } catch (Exception e) {
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

            List<IssueResponse> issues = client.fetchIssues(owner, repo);

            return issues.stream()
                .filter(issue -> issue.createdAt().isAfter(lastCheckTime))
                .collect(Collectors.toMap(
                    issue -> String.format(NEW_ISSUE_ANSWER, issue.title(), issue.createdAt()),
                    IssueResponse::createdAt
                ));
        }

        return Map.of();
    }
}
