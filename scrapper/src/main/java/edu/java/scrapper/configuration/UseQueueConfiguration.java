package edu.java.scrapper.configuration;

import edu.java.scrapper.service.client.botClient.request.LinkUpdateRequest;
import edu.java.scrapper.service.linkUpdateService.linkUpdateSender.LinkUpdateSender;
import edu.java.scrapper.service.linkUpdateService.linkUpdateSender.ScrapperQueueProducer;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaTemplate;

@Configuration
@ConditionalOnProperty(prefix = "app", name = "use-queue", havingValue = "true")
public class UseQueueConfiguration {

    @Bean
    public LinkUpdateSender linkUpdateSender(
        KafkaTemplate<String, LinkUpdateRequest> kafkaTemplate,
        ApplicationConfig applicationConfig
    ) {
        return new ScrapperQueueProducer(kafkaTemplate, applicationConfig);
    }

    @Bean
    public NewTopic botTopic(ApplicationConfig applicationConfig) {
        ApplicationConfig.Topic botTopic = applicationConfig.botTopic();

        return TopicBuilder.name(botTopic.name())
            .partitions(botTopic.partitions())
            .replicas(botTopic.replicas())
            .build();
    }
}
