package edu.java.scrapper.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.java.scrapper.service.api.controller.response.ApiErrorResponse;
import edu.java.scrapper.service.client.botClient.BotClient;
import edu.java.scrapper.service.client.botClient.ResponseErrorException;
import edu.java.scrapper.service.client.gitHubClient.GitHubClient;
import edu.java.scrapper.service.client.stackOverflowClient.StackOverflowClient;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.client.ReactorNettyClientRequestFactory;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.support.WebClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

@Configuration
@Slf4j
public class ClientConfiguration {

    @Bean
    public BotClient botClient(@Value("${api.bot.baseurl}") String baseURL) {
        RestClient restClient = RestClient.builder()
            .requestFactory(new ReactorNettyClientRequestFactory())
            .defaultStatusHandler(HttpStatusCode::isError, (request, response) -> {
                ObjectMapper objectMapper = new ObjectMapper();

                try {
                    ApiErrorResponse apiErrorResponse =
                        objectMapper.readValue(response.getBody(), ApiErrorResponse.class);
                    throw new ResponseErrorException(apiErrorResponse);
                } catch (IOException e) {
                    log.error("Get incorrect error response {}\nException {}", response, e);
                    throw new ResponseErrorException(null);
                }
            }).baseUrl(baseURL).build();
        RestClientAdapter adapter = RestClientAdapter.create(restClient);
        HttpServiceProxyFactory factory = HttpServiceProxyFactory.builderFor(adapter).build();

        return factory.createClient(BotClient.class);
    }

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
