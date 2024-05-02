package edu.java.scrapper.domain.dao.jooq;

import edu.java.scrapper.IntegrationTest;
import edu.java.scrapper.domain.dto.Link;
import java.net.URI;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.jooq.DSLContext;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import static edu.java.scrapper.domain.jooq.Tables.LINK;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

@SpringBootTest(properties = { "app.database-access-type=jooq", "app.use-queue=false" })
public class JooqLinkDaoTest extends IntegrationTest {

    @Autowired
    JooqLinkDao linkDao;
    @Autowired
    DSLContext dslContext;

    @Test
    @Rollback
    @Transactional
    @DisplayName("Добавление link по url")
    void add_shouldAddLinkByUrl() {
        URI url = URI.create("https://edu.tinkoff.ru/");

        Link result = linkDao.add(url);
        Link realResult = dslContext.selectFrom(LINK)
            .fetchOneInto(Link.class);

        assertThat(result)
            .isEqualTo(realResult);
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
    void add_shouldNotAddExistingLink() {
        URI url = URI.create("https://edu.tinkoff.ru/");
        Link expected = linkDao.add(url);

        Link result = linkDao.add(url);
        Link realResult = dslContext.selectFrom(LINK)
            .fetchOneInto(Link.class);

        assertThat(result)
            .isNotNull()
            .isEqualTo(expected)
            .isEqualTo(realResult);
    }

    @Test
    @Rollback
    @Transactional
    @DisplayName("Добавление link по url и last_check_at")
    void add_shouldAddLinkByUrlAndLastCheckAt() {
        URI url = URI.create("https://edu.tinkoff.ru/");
        OffsetDateTime time = OffsetDateTime.now().withNano(0).minusDays(1);

        Link result = linkDao.add(url, time);
        Link realResult = dslContext.selectFrom(LINK)
            .fetchOneInto(Link.class);

        assertThat(result)
            .isEqualTo(realResult);
        assertThat(result.url())
            .isEqualTo(url);
        assertThat(result.lastCheckAt())
            .isEqualTo(time);
    }

    @Test
    @Rollback
    @Transactional
    @DisplayName("Замена last_check_at")
    void add_shouldReplaceTimeIfUrlExists() {
        URI url = URI.create("https://edu.tinkoff.ru/");
        OffsetDateTime time = OffsetDateTime.now().withNano(0).minusDays(1);
        linkDao.add(url);

        Link result = linkDao.add(url, time);
        Link realResult = dslContext.selectFrom(LINK)
            .fetchOneInto(Link.class);

        assertThat(result)
            .isEqualTo(realResult);
        assertThat(result.url())
            .isEqualTo(url);
        assertThat(result.lastCheckAt())
            .isEqualTo(time);
    }

    @Test
    @Rollback
    @Transactional
    @DisplayName("Удаление link")
    void remove_shouldDeleteExistingLink() {
        URI url = URI.create("https://edu.tinkoff.ru/");
        Link expected = linkDao.add(url);

        Link result = linkDao.remove(url);
        Link realResult = dslContext.selectFrom(LINK)
            .fetchOptionalInto(Link.class).orElse(null);

        assertThat(realResult)
            .isNull();
        assertThat(result)
            .isEqualTo(expected);
    }

    @Test
    @Rollback
    @Transactional
    @DisplayName("Удаление несуществующего link")
    void remove_shouldNotDeleteNotExistingLink() {
        URI url = URI.create("https://edu.tinkoff.ru/");

        Link result = linkDao.remove(url);

        assertThat(result)
            .isNull();
    }

    @Test
    @Rollback
    @Transactional
    @DisplayName("Поиск link")
    void find_shouldFindLink() {
        List<URI> urls = List.of(
            URI.create("https://edu.tinkoff.ru/"),
            URI.create("https://github.com/"),
            URI.create("https://lk.etu.ru/")
        );
        List<Link> expected = new ArrayList<>();
        for (URI url : urls) {
            expected.add(linkDao.add(url));
        }

        for (int i = 0; i < urls.size(); ++i) {
            Link resultUrl = linkDao.find(urls.get(i));
            Link resultId = linkDao.find(expected.get(i).id());

            assertThat(resultUrl)
                .isNotNull();
            assertThat(resultUrl)
                .isEqualTo(resultId);
            assertThat(expected)
                .contains(resultUrl);
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
            linkDao.add(url);
        }

        Link result = linkDao.find(URI.create("https://digital.etu.ru/"));

        assertThat(result)
            .isNull();
    }

    @Test
    @Rollback
    @Transactional
    @DisplayName("Список link")
    void findAll_shouldReturnAllLinks() {
        List<URI> expected = List.of(
            URI.create("https://edu.tinkoff.ru/"),
            URI.create("https://github.com/"),
            URI.create("https://lk.etu.ru/")
        );
        for (URI url : expected) {
            linkDao.add(url);
        }

        List<Link> result = linkDao.findAll();

        assertThat(result)
            .isNotNull();
        assertThat(result.stream().map(Link::url))
            .isEqualTo(expected);
    }

    @Test
    @Rollback
    @Transactional
    @DisplayName("Пустой список link")
    void findAll_shouldReturnEmptyListIfLinksNotExist() {
        List<Link> result = linkDao.findAll();

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
        for (Map.Entry<URI, OffsetDateTime> link : links.entrySet()) {
            linkDao.add(link.getKey(), link.getValue());
        }

        List<Link> result = linkDao.findAll(base);

        assertThat(result)
            .isNotNull()
            .hasSize(2);
        assertThat(result.stream().map(Link::url).toList())
            .contains(URI.create("https://github.com/"), URI.create("https://edu.tinkoff.ru/"));
    }
}
