package edu.java.bot.configuration;

import edu.java.bot.controller.response.ApiErrorResponse;
import edu.java.bot.service.client.scrapperClient.ResponseErrorException;
import edu.java.bot.service.client.scrapperClient.ScrapperClient;
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
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;
import reactor.util.retry.RetryBackoffSpec;

@Configuration
@Slf4j
@RequiredArgsConstructor
public class ClientConfiguration {

    private final ApplicationConfig applicationConfig;

    @Bean
    public ScrapperClient scrapperClient(@Value("${api.scrapper.baseurl}") String baseURL) {
        WebClient webClient = WebClient.builder()
            .filter(withRetryableRequests())
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

        return factory.createClient(ScrapperClient.class);
    }

    private ExchangeFilterFunction withRetryableRequests() {
        List<Integer> responseCodes = applicationConfig.retryConfig().responseCodes();

        return (request, next) -> next.exchange(request)
            .flatMap(clientResponse -> Mono.just(clientResponse)
                .filter(response -> responseCodes.contains(clientResponse.statusCode().value()))
                .flatMap(response -> clientResponse.createException())
                .flatMap(Mono::error)
                .thenReturn(clientResponse))
            .retryWhen(retryBackoffSpec());
    }

    private RetryBackoffSpec retryBackoffSpec() {
        return getRetryPolicy()
            .filter(throwable -> throwable instanceof WebClientResponseException)
            .doBeforeRetry(retrySignal -> log.warn(
                "Retrying request after following exception : {}",
                retrySignal.failure().getLocalizedMessage()
            ))
            .onRetryExhaustedThrow((retryBackoffSpec, retrySignal) -> retrySignal.failure());
    }

    private RetryBackoffSpec getRetryPolicy() {
        Integer maxAttempts = applicationConfig.retryConfig().maxAttempts();
        Duration delayTime = applicationConfig.retryConfig().delayTime();

        return switch (applicationConfig.retryConfig().delayType()) {
            case FIXED -> Retry.fixedDelay(maxAttempts, delayTime);
            case EXPONENTIAL -> Retry.backoff(maxAttempts, delayTime);
        };
    }
}
