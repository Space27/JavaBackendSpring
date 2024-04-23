package edu.java.scrapper.configuration;

import edu.java.scrapper.controller.response.ApiErrorResponse;
import edu.java.scrapper.service.client.bot.BotClient;
import edu.java.scrapper.service.client.bot.ResponseErrorException;
import edu.java.scrapper.service.client.gitHub.GitHubClient;
import edu.java.scrapper.service.client.stackOverflow.StackOverflowClient;
import java.time.Duration;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.reactive.function.client.support.WebClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;
import reactor.core.Exceptions;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;
import reactor.util.retry.RetryBackoffSpec;

@Configuration
@Slf4j
@RequiredArgsConstructor
public class ClientConfiguration {

    private static final String RETRY_LOG_MESSAGE = "Retrying request after following exception : {}";

    @Bean
    public BotClient botClient(
        @Value("${api.bot.baseurl}") String baseURL,
        @Value("app.retry-config") ApplicationConfig.RetryConfig retryConfig
    ) {
        WebClient webClient = WebClient.builder()
            .filter(withRetryableRequests(retryConfig))
            .defaultStatusHandler(
                HttpStatusCode::isError,
                resp -> {
                    ApiErrorResponse empty = new ApiErrorResponse(null, null, null, null, null);
                    return resp.bodyToMono(ApiErrorResponse.class).switchIfEmpty(Mono.just(empty))
                        .flatMap(errorBody -> {
                            if (errorBody.code() == null) {
                                return resp.createError();
                            } else {
                                return Mono.error(new ResponseErrorException(errorBody));
                            }
                        });

                }
            )
            .baseUrl(baseURL).build();
        WebClientAdapter adapter = WebClientAdapter.create(webClient);
        HttpServiceProxyFactory factory = HttpServiceProxyFactory.builderFor(adapter).build();

        return factory.createClient(BotClient.class);
    }

    @Bean
    public GitHubClient gitHubClient(
        @Value("${api.github.baseurl}") String baseURL,
        @Value("app.retry-config") ApplicationConfig.RetryConfig retryConfig
    ) {
        WebClient webClient = WebClient.builder().filter(withRetryableRequests(retryConfig)).baseUrl(baseURL).build();
        WebClientAdapter adapter = WebClientAdapter.create(webClient);
        HttpServiceProxyFactory factory = HttpServiceProxyFactory.builderFor(adapter).build();

        return factory.createClient(GitHubClient.class);
    }

    @Bean
    public StackOverflowClient stackOverflowClient(
        @Value("${api.stackoverflow.baseurl}") String baseURL,
        @Value("app.retry-config") ApplicationConfig.RetryConfig retryConfig
    ) {
        WebClient webClient = WebClient.builder().filter(withRetryableRequests(retryConfig)).baseUrl(baseURL).build();
        WebClientAdapter adapter = WebClientAdapter.create(webClient);
        HttpServiceProxyFactory factory = HttpServiceProxyFactory.builderFor(adapter).build();

        return factory.createClient(StackOverflowClient.class);
    }

    private ExchangeFilterFunction withRetryableRequests(ApplicationConfig.RetryConfig retryConfig) {
        List<Integer> responseCodes = retryConfig.responseCodes();

        return (request, next) -> next.exchange(request)
            .flatMap(clientResponse -> Mono.just(clientResponse)
                .filter(response -> responseCodes.contains(clientResponse.statusCode().value()))
                .flatMap(response -> clientResponse.createException())
                .flatMap(Mono::error)
                .thenReturn(clientResponse))
            .retryWhen(retryBackoffSpec(retryConfig));
    }

    private Retry retryBackoffSpec(ApplicationConfig.RetryConfig retryConfig) {
        Retry retry = getRetryPolicy(retryConfig);

        if (retry instanceof RetryBackoffSpec retryBackoff) {
            return retryBackoff
                .filter(throwable -> throwable instanceof WebClientResponseException)
                .doBeforeRetry(retrySignal -> log.warn(RETRY_LOG_MESSAGE, retrySignal.failure().getLocalizedMessage()))
                .onRetryExhaustedThrow((retryBackoffSpec, retrySignal) -> retrySignal.failure());
        }

        return retry;
    }

    private Retry getRetryPolicy(ApplicationConfig.RetryConfig retryConfig) {
        Integer maxAttempts = retryConfig.maxAttempts();
        Duration delayTime = retryConfig.delayTime();

        return switch (retryConfig.delayType()) {
            case FIXED -> Retry.fixedDelay(maxAttempts, delayTime);
            case EXPONENTIAL -> Retry.backoff(maxAttempts, delayTime);
            case LINEAR ->
                RetryBackoffSpec.from(flux -> flux.flatMap(rs -> getLinearRetry(rs, maxAttempts, delayTime)));
        };
    }

    private Mono<Long> getLinearRetry(Retry.RetrySignal rs, Integer maxAttempt, Duration delayTime) {
        if (rs.totalRetries() < maxAttempt) {
            Duration delay = delayTime.multipliedBy(rs.totalRetries());
            log.warn(RETRY_LOG_MESSAGE, rs.failure().getLocalizedMessage());
            return Mono.delay(delay)
                .thenReturn(rs.totalRetries());
        } else {
            throw Exceptions.propagate(rs.failure());
        }
    }
}
