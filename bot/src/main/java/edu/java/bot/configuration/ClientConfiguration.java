package edu.java.bot.configuration;

import edu.java.bot.service.api.schemas.ApiErrorResponse;
import edu.java.bot.service.scrapperClient.ResponseErrorException;
import edu.java.bot.service.scrapperClient.ScrapperClient;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.support.WebClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;
import reactor.core.publisher.Mono;

@Configuration
@Slf4j
public class ClientConfiguration {

    private final static long GET_TIME_LIMIT = 10000;

    @Bean
    public ScrapperClient scrapperClient(ApplicationConfig applicationConfig) {
        WebClient webClient = WebClient.builder()
            .defaultStatusHandler(HttpStatusCode::isError, resp -> {
                Mono<ApiErrorResponse> mono = resp.bodyToMono(ApiErrorResponse.class);

                ApiErrorResponse apiErrorResponse;
                try {
                    apiErrorResponse = mono.toFuture().get(GET_TIME_LIMIT, TimeUnit.MILLISECONDS);
                } catch (InterruptedException | TimeoutException | ExecutionException e) {
                    log.warn("Get incorrect error response {}", resp);
                    throw new ResponseErrorException(null);
                }

                throw new ResponseErrorException(apiErrorResponse);
            })
            .baseUrl(applicationConfig.scrapper().baseURL()).build();
        WebClientAdapter adapter = WebClientAdapter.create(webClient);
        HttpServiceProxyFactory factory = HttpServiceProxyFactory.builderFor(adapter).build();

        return factory.createClient(ScrapperClient.class);
    }
}
