package edu.java.bot.service;

import edu.java.bot.controller.request.LinkUpdateRequest;
import edu.java.bot.controller.updatesApi.UpdateHandler;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.kafka.retrytopic.DltStrategy;
import org.springframework.kafka.support.Acknowledgment;

@RequiredArgsConstructor
public class ScrapperListener {

    private final UpdateHandler updateHandler;

    @RetryableTopic(attempts = "1",
                    dltStrategy = DltStrategy.ALWAYS_RETRY_ON_ERROR,
                    concurrency = "1",
                    autoCreateTopics = "false",
                    dltTopicSuffix = "-dlq")
    @KafkaListener(topics = "${app.scrapper-topic.name}", concurrency = "1")
    public void listen(@Valid LinkUpdateRequest update, Acknowledgment acknowledgment) {
        updateHandler.handleUpdate(update);
        acknowledgment.acknowledge();
    }
}
