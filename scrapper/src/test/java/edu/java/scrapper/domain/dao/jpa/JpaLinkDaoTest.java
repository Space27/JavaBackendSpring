package edu.java.scrapper.domain.dao.jpa;

import edu.java.scrapper.IntegrationTest;
import edu.java.scrapper.domain.dao.jpa.entity.LinkEntity;
import edu.java.scrapper.domain.dto.Link;
import java.net.URI;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest(properties = { "app.database-access-type=jpa", "app.use-queue=false" })
public class JpaLinkDaoTest extends IntegrationTest {

    @Autowired
    JdbcClient jdbcClient;
    @Autowired
    JpaLinkDao linkDao;

    @Test
    @Rollback
    @Transactional
    @DisplayName("Добавление link по url")
    void save_shouldAddLinkByUrl() {
        URI url = URI.create("https://edu.tinkoff.ru/");
        LinkEntity link = new LinkEntity();
        link.setUrl(url.toString());

        linkDao.saveAndFlush(link);
        Link result = jdbcClient.sql("SELECT * FROM link")
            .query(Link.class).single();

        assertThat(result.url())
            .isEqualTo(url);
        assertThat(result.lastCheckAt())
            .isEqualTo(result.createdAt());
        assertThat(result.lastCheckAt())
            .isCloseTo(OffsetDateTime.now(), within(5, ChronoUnit.SECONDS));
        assertThat(result.id())
            .isGreaterThanOrEqualTo(1L);
    }

    @Test
    @Rollback
    @Transactional
    @DisplayName("Повторное добавление link по url")
    void save_shouldNotAddExistingLink() {
        URI url = URI.create("https://edu.tinkoff.ru/");
        LinkEntity link = new LinkEntity();
        link.setUrl(url.toString());
        linkDao.saveAndFlush(link);

        boolean wasAdded = linkDao.existsByUrl(url.toString());
        link = new LinkEntity();
        link.setUrl(url.toString());
        LinkEntity finalLink = link;
        assertThat(wasAdded)
            .isTrue();
        assertThrows(DataIntegrityViolationException.class, () -> linkDao.saveAndFlush(finalLink));
    }

    @Test
    @Rollback
    @Transactional
    @DisplayName("Замена last_check_at")
    void save_shouldReplaceTimeIfUrlExists() {
        OffsetDateTime time = OffsetDateTime.now().withNano(0).minusDays(1);
        URI url = URI.create("https://edu.tinkoff.ru/");
        LinkEntity link = new LinkEntity();
        link.setUrl(url.toString());
        linkDao.saveAndFlush(link);

        link = linkDao.findByUrl(url.toString()).orElse(null);
        link.setLastCheckAt(time);
        linkDao.saveAndFlush(link);
        Link result = jdbcClient.sql("SELECT * FROM link")
            .query(Link.class).single();

        assertThat(result.url())
            .isEqualTo(url);
        assertThat(result.lastCheckAt())
            .isEqualTo(time);
    }

    @Test
    @Rollback
    @Transactional
    @DisplayName("Удаление link")
    void delete_shouldDeleteExistingLink() {
        URI url = URI.create("https://edu.tinkoff.ru/");
        LinkEntity link = new LinkEntity();
        link.setUrl(url.toString());
        linkDao.saveAndFlush(link);

        linkDao.deleteByUrl(url.toString());
        linkDao.flush();
        Link result = jdbcClient.sql("SELECT * FROM link")
            .query(Link.class).optional().orElse(null);

        assertThat(result)
            .isNull();
    }

    @Test
    @Rollback
    @Transactional
    @DisplayName("Поиск нескольких link")
    void find_shouldFindLink() {
        URI url = URI.create("https://edu.tinkoff.ru/");
        LinkEntity link = new LinkEntity();
        link.setUrl(url.toString());
        linkDao.saveAndFlush(link);

        assertThat(linkDao.findById(link.getId()))
            .isEqualTo(linkDao.findByUrl(url.toString()));
        assertThat(linkDao.findById(link.getId()).orElse(null))
            .isEqualTo(link);
    }

    @Test
    @Rollback
    @Transactional
    @DisplayName("Поиск нескольких link")
    void find_shouldFindLinks() {
        List<URI> urls = List.of(
            URI.create("https://edu.tinkoff.ru/"),
            URI.create("https://github.com/"),
            URI.create("https://lk.etu.ru/")
        );
        for (URI url : urls) {
            LinkEntity link = new LinkEntity();
            link.setUrl(url.toString());
            linkDao.saveAndFlush(link);
        }

        for (URI url : urls) {
            LinkEntity find = linkDao.findByUrl(url.toString()).orElse(null);

            assertThat(find)
                .isNotNull();
            assertThat(find.getUrl())
                .isEqualTo(url.toString());
        }
    }

    @Test
    @Rollback
    @Transactional
    @DisplayName("Поиск несуществующего link")
    void find_shouldNotFindNotExistingLink() {
        List<URI> urls = List.of(
            URI.create("https://edu.tinkoff.ru/"),
            URI.create("https://github.com/"),
            URI.create("https://lk.etu.ru/")
        );
        for (URI url : urls) {
            LinkEntity link = new LinkEntity();
            link.setUrl(url.toString());
            linkDao.saveAndFlush(link);
        }

        LinkEntity result = linkDao.findByUrl("https://digital.etu.ru/").orElse(null);

        assertThat(result)
            .isNull();
    }

    @Test
    @Rollback
    @Transactional
    @DisplayName("Список link")
    void findAll_shouldReturnAllLinks() {
        List<String> expected = List.of(
            "https://edu.tinkoff.ru/",
            "https://github.com/",
            "https://lk.etu.ru/"
        );
        for (String url : expected) {
            LinkEntity link = new LinkEntity();
            link.setUrl(url);
            linkDao.saveAndFlush(link);
        }

        List<LinkEntity> result = linkDao.findAll();

        assertThat(result)
            .isNotNull();
        assertThat(result.stream().map(LinkEntity::getUrl))
            .isEqualTo(expected);
    }

    @Test
    @Rollback
    @Transactional
    @DisplayName("Пустой список link")
    void findAll_shouldReturnEmptyListIfLinksNotExist() {
        List<LinkEntity> result = linkDao.findAll();

        assertThat(result)
            .isNotNull()
            .isEmpty();
    }

    @Test
    @Rollback
    @Transactional
    @DisplayName("Список link по времени")
    void findAll_shouldReturnAllLinksWithMinTime() {
        OffsetDateTime base = OffsetDateTime.now();
        Map<URI, OffsetDateTime> links = Map.of(
            URI.create("https://edu.tinkoff.ru/"), base.minusSeconds(1),
            URI.create("https://github.com/"), base,
            URI.create("https://lk.etu.ru/"), base.plusSeconds(1)
        );
        for (Map.Entry<URI, OffsetDateTime> entry : links.entrySet()) {
            LinkEntity link = new LinkEntity();
            link.setUrl(entry.getKey().toString());
            linkDao.saveAndFlush(link);
            link = linkDao.findByUrl(entry.getKey().toString()).orElse(null);
            link.setLastCheckAt(entry.getValue());
            linkDao.saveAndFlush(link);
        }

        List<LinkEntity> result = linkDao.findLinkEntitiesByLastCheckAtLessThanEqual(base);

        assertThat(result)
            .isNotNull()
            .hasSize(2);
        assertThat(result.stream().map(LinkEntity::getUrl).toList())
            .contains("https://github.com/", "https://edu.tinkoff.ru/");
    }
}
