package edu.java.bot.configuration;

import edu.java.bot.controller.response.ApiErrorResponse;
import edu.java.bot.service.client.scrapper.ResponseErrorException;
import edu.java.bot.service.client.scrapper.ScrapperClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
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

    @Bean
    public ScrapperClient scrapperClient(@Value("${api.scrapper.baseurl}") String baseURL) {
        WebClient webClient = WebClient.builder()
            .defaultStatusHandler(
                HttpStatusCode::isError,
                resp -> {
                    ApiErrorResponse empty = new ApiErrorResponse(null, null, null, null, null);
                    return resp.bodyToMono(ApiErrorResponse.class).switchIfEmpty(Mono.just(empty))
                        .flatMap(errorBody -> {
                            if (errorBody.code() == null) {
                                return Mono.error(new ResponseErrorException(null));
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
}
