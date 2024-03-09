package edu.java.bot.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.java.bot.controller.response.ApiErrorResponse;
import edu.java.bot.service.client.scrapperClient.ResponseErrorException;
import edu.java.bot.service.client.scrapperClient.ScrapperClient;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.client.ReactorNettyClientRequestFactory;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

@Configuration
@Slf4j
public class ClientConfiguration {

    @Bean
    public ScrapperClient scrapperClient(@Value("${api.scrapper.baseurl}") String baseURL) {
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

        return factory.createClient(ScrapperClient.class);
    }
}
