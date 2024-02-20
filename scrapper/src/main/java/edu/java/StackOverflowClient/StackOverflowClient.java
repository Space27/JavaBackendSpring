package edu.java.StackOverflowClient;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.service.annotation.GetExchange;

public interface StackOverflowClient {

    @GetExchange("/questions/{id}?site=stackoverflow")
    QuestionResponse fetchQuestion(@PathVariable Long id);
}
