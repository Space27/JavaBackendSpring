package edu.java.scrapper.configuration;

import edu.java.scrapper.domain.dao.jpa.JpaChatLinkDao;
import edu.java.scrapper.domain.dao.jpa.JpaLinkDao;
import edu.java.scrapper.domain.dao.jpa.JpaTgChatDao;
import edu.java.scrapper.service.daoService.LinkService;
import edu.java.scrapper.service.daoService.TgChatService;
import edu.java.scrapper.service.daoService.jpa.JpaLinkService;
import edu.java.scrapper.service.daoService.jpa.JpaTgChatService;
import edu.java.scrapper.service.linkUpdateService.BotService;
import edu.java.scrapper.service.linkUpdateService.clientUpdate.ClientUpdater;
import edu.java.scrapper.service.linkUpdateService.linkUpdater.JpaLinkUpdater;
import edu.java.scrapper.service.linkUpdateService.linkUpdater.LinkUpdater;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories
@ConditionalOnProperty(prefix = "app", name = "database-access-type", havingValue = "jpa")
public class JpaAccessConfiguration {

    @Bean
    public TgChatService tgChatService(JpaTgChatDao jpaTgChatDao, JpaChatLinkDao jpaChatLinkDao) {
        return new JpaTgChatService(jpaTgChatDao, jpaChatLinkDao);
    }

    @Bean
    public LinkService linkService(
        JpaTgChatDao jpaTgChatDao, JpaLinkDao jpaLinkDao, JpaChatLinkDao jpaChatLinkDao
    ) {
        return new JpaLinkService(jpaChatLinkDao, jpaLinkDao, jpaTgChatDao);
    }

    @Bean
    public LinkUpdater linkUpdater(
        JpaLinkDao jpaLinkDao, JpaChatLinkDao jpaChatLinkDao, ClientUpdater clientUpdater, BotService botService
    ) {
        return new JpaLinkUpdater(clientUpdater, botService, jpaLinkDao, jpaChatLinkDao);
    }
}
