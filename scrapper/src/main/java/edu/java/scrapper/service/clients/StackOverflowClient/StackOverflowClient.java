package edu.java.scrapper.service.clients.StackOverflowClient;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.service.annotation.GetExchange;

public interface StackOverflowClient {

    @GetExchange("/questions/{id}?site=stackoverflow")
    QuestionResponse fetchQuestion(@PathVariable Long id);
}
