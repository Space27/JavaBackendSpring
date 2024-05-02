package edu.java.scrapper.service.linkUpdateService.linkUpdateSender;

import edu.java.scrapper.configuration.ApplicationConfig;
import edu.java.scrapper.service.client.bot.request.LinkUpdateRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;

@RequiredArgsConstructor
public class ScrapperQueueProducer implements LinkUpdateSender {

    private final KafkaTemplate<String, LinkUpdateRequest> kafkaTemplate;
    private final ApplicationConfig applicationConfig;

    public void send(LinkUpdateRequest linkUpdate) {
        kafkaTemplate.send(applicationConfig.botTopic().name(), linkUpdate);
    }
}
