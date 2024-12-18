package edu.java.scrapper.service.client.stackOverflow;

import edu.java.scrapper.service.client.stackOverflow.dto.AnswerListResponse;
import edu.java.scrapper.service.client.stackOverflow.dto.QuestionResponse;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.service.annotation.GetExchange;

public interface StackOverflowClient {

    @GetExchange("/questions/{id}?site=stackoverflow")
    QuestionResponse fetchQuestion(@PathVariable Long id);

    @GetExchange("/questions/{id}/answers?site=stackoverflow")
    AnswerListResponse fetchAnswers(@PathVariable Long id);
}
