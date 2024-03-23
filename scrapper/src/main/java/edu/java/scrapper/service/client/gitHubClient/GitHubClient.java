package edu.java.scrapper.service.client.gitHubClient;

import edu.java.scrapper.service.client.gitHubClient.dto.IssueResponse;
import edu.java.scrapper.service.client.gitHubClient.dto.RepositoryResponse;
import java.util.List;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.service.annotation.GetExchange;

public interface GitHubClient {

    @GetExchange("/repos/{owner}/{repo}")
    RepositoryResponse fetchRepository(@PathVariable String owner, @PathVariable String repo);

    @GetExchange("/repos/{owner}/{repo}/issues")
    List<IssueResponse> fetchIssues(@PathVariable String owner, @PathVariable String repo);

}
