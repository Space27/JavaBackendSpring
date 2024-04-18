package edu.java.scrapper.configuration;

import edu.java.scrapper.domain.dao.jdbc.JdbcChatLinkDao;
import edu.java.scrapper.domain.dao.jdbc.JdbcLinkDao;
import edu.java.scrapper.domain.dao.jdbc.JdbcTgChatDao;
import edu.java.scrapper.service.daoService.LinkService;
import edu.java.scrapper.service.daoService.TgChatService;
import edu.java.scrapper.service.daoService.jdbc.JdbcLinkService;
import edu.java.scrapper.service.daoService.jdbc.JdbcTgChatService;
import edu.java.scrapper.service.linkUpdateService.BotService;
import edu.java.scrapper.service.linkUpdateService.clientUpdate.ClientUpdater;
import edu.java.scrapper.service.linkUpdateService.linkUpdater.JdbcLinkUpdater;
import edu.java.scrapper.service.linkUpdateService.linkUpdater.LinkUpdater;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.simple.JdbcClient;

@Configuration
@ConditionalOnProperty(prefix = "app", name = "database-access-type", havingValue = "jdbc")
public class JdbcAccessConfiguration {

    @Bean
    public JdbcLinkDao jdbcLinkDao(JdbcClient jdbcClient) {
        return new JdbcLinkDao(jdbcClient);
    }

    @Bean
    public JdbcTgChatDao jdbcTgChatDao(JdbcClient jdbcClient) {
        return new JdbcTgChatDao(jdbcClient);
    }

    @Bean
    public JdbcChatLinkDao jdbcChatLinkDao(JdbcClient jdbcClient) {
        return new JdbcChatLinkDao(jdbcClient);
    }

    @Bean
    public TgChatService tgChatService(JdbcTgChatDao jdbcTgChatDao) {
        return new JdbcTgChatService(jdbcTgChatDao);
    }

    @Bean
    public LinkService linkService(
        JdbcTgChatDao jdbcTgChatDao, JdbcLinkDao jdbcLinkDao, JdbcChatLinkDao jdbcChatLinkDao
    ) {
        return new JdbcLinkService(jdbcLinkDao, jdbcTgChatDao, jdbcChatLinkDao);
    }

    @Bean
    public LinkUpdater linkUpdater(
        JdbcLinkDao jdbcLinkDao, JdbcChatLinkDao jdbcChatLinkDao, ClientUpdater clientUpdater, BotService botService
    ) {
        return new JdbcLinkUpdater(clientUpdater, botService, jdbcLinkDao, jdbcChatLinkDao);
    }
}
