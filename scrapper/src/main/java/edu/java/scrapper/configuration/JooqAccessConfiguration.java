package edu.java.scrapper.configuration;

import edu.java.scrapper.domain.dao.jooq.JooqChatLinkDao;
import edu.java.scrapper.domain.dao.jooq.JooqLinkDao;
import edu.java.scrapper.domain.dao.jooq.JooqTgChatDao;
import edu.java.scrapper.service.daoService.LinkService;
import edu.java.scrapper.service.daoService.TgChatService;
import edu.java.scrapper.service.daoService.jooq.JooqLinkService;
import edu.java.scrapper.service.daoService.jooq.JooqTgChatService;
import edu.java.scrapper.service.linkUpdateService.BotService;
import edu.java.scrapper.service.linkUpdateService.clientUpdate.ClientUpdater;
import edu.java.scrapper.service.linkUpdateService.linkUpdater.JooqLinkUpdater;
import edu.java.scrapper.service.linkUpdateService.linkUpdater.LinkUpdater;
import org.jooq.DSLContext;
import org.jooq.conf.RenderQuotedNames;
import org.jooq.impl.DefaultConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.jooq.DefaultConfigurationCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(prefix = "app", name = "database-access-type", havingValue = "jooq")
public class JooqAccessConfiguration {

    @Bean
    public DefaultConfigurationCustomizer postgresJooqCustomizer() {
        return (DefaultConfiguration c) -> c.settings()
            .withRenderSchema(false)
            .withRenderFormatted(true)
            .withRenderQuotedNames(RenderQuotedNames.NEVER);
    }

    @Bean
    public JooqTgChatDao jooqTgChatDao(DSLContext dslContext) {
        return new JooqTgChatDao(dslContext);
    }

    @Bean
    public JooqLinkDao jooqLinkDao(DSLContext dslContext) {
        return new JooqLinkDao(dslContext);
    }

    @Bean
    public JooqChatLinkDao jooqChatLinkDao(DSLContext dslContext) {
        return new JooqChatLinkDao(dslContext);
    }

    @Bean
    public TgChatService tgChatService(JooqTgChatDao jooqTgChatDao) {
        return new JooqTgChatService(jooqTgChatDao);
    }

    @Bean
    public LinkService linkService(
        JooqTgChatDao jooqTgChatDao, JooqLinkDao jooqLinkDao, JooqChatLinkDao jooqChatLinkDao
    ) {
        return new JooqLinkService(jooqLinkDao, jooqTgChatDao, jooqChatLinkDao);
    }

    @Bean
    public LinkUpdater linkUpdater(
        JooqLinkDao jooqLinkDao, JooqChatLinkDao jooqChatLinkDao, ClientUpdater clientUpdater, BotService botService
    ) {
        return new JooqLinkUpdater(clientUpdater, botService, jooqLinkDao, jooqChatLinkDao);
    }
}
