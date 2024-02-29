package edu.java.scrapper.service.client.gitHubClient;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.service.annotation.GetExchange;

public interface GitHubClient {

    @GetExchange("/repos/{owner}/{repo}")
    RepositoryResponse fetchRepository(@PathVariable String owner, @PathVariable String repo);
}
