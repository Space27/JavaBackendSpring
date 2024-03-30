package edu.java.scrapper.configuration;

import edu.java.scrapper.service.client.botClient.BotClient;
import edu.java.scrapper.service.linkUpdateService.linkUpdateSender.BotClientSender;
import edu.java.scrapper.service.linkUpdateService.linkUpdateSender.LinkUpdateSender;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(prefix = "app", name = "use-queue", havingValue = "false")
public class DoNotUseQueueConfiguration {

    @Bean
    public LinkUpdateSender linkUpdateSender(BotClient botClient) {
        return new BotClientSender(botClient);
    }
}
