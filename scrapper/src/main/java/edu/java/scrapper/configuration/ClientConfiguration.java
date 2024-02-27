package edu.java.scrapper.configuration;

import edu.java.scrapper.service.clients.GitHubClient.GitHubClient;
import edu.java.scrapper.service.clients.StackOverflowClient.StackOverflowClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.support.WebClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

@Configuration
public class ClientConfiguration {

    @Bean
    public GitHubClient gitHubClient(@Value("${api.github.baseurl}") String baseURL) {
        WebClient webClient = WebClient.builder().baseUrl(baseURL).build();
        WebClientAdapter adapter = WebClientAdapter.create(webClient);
        HttpServiceProxyFactory factory = HttpServiceProxyFactory.builderFor(adapter).build();

        return factory.createClient(GitHubClient.class);
    }

    @Bean
    public StackOverflowClient stackOverflowClient(@Value("${api.stackoverflow.baseurl}") String baseURL) {
        WebClient webClient = WebClient.builder().baseUrl(baseURL).build();
        WebClientAdapter adapter = WebClientAdapter.create(webClient);
        HttpServiceProxyFactory factory = HttpServiceProxyFactory.builderFor(adapter).build();

        return factory.createClient(StackOverflowClient.class);
    }
}
