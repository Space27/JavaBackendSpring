package edu.java.scrapper.service.client.gitHub;

import edu.java.scrapper.service.client.gitHub.dto.IssueResponse;
import edu.java.scrapper.service.client.gitHub.dto.RepositoryResponse;
import java.util.List;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.service.annotation.GetExchange;

public interface GitHubClient {

    @GetExchange("/repos/{owner}/{repo}")
    RepositoryResponse fetchRepository(@PathVariable String owner, @PathVariable String repo);

    @GetExchange("/repos/{owner}/{repo}/issues")
    List<IssueResponse> fetchIssues(@PathVariable String owner, @PathVariable String repo);

}
