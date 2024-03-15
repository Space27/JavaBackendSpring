package edu.java.scrapper.domain.dao.jooq;

import edu.java.scrapper.domain.dao.LinkDao;
import edu.java.scrapper.domain.dto.Link;
import java.net.URI;
import java.time.OffsetDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import static edu.java.scrapper.domain.jooq.Tables.CHAT_LINK;
import static edu.java.scrapper.domain.jooq.Tables.LINK;
import static org.jooq.impl.DSL.excluded;
import static org.jooq.impl.DSL.select;

@Repository
@RequiredArgsConstructor
public class JooqLinkDao implements LinkDao {

    private final DSLContext dslContext;

    @Override
    public Link add(URI url) {
        return dslContext.insertInto(LINK)
            .columns(LINK.URL)
            .values(url.toString())
            .onConflict(LINK.URL)
            .doUpdate()
            .set(LINK.URL, excluded(LINK.URL))
            .returning()
            .fetchOneInto(Link.class);
    }

    @Override
    public Link add(URI url, OffsetDateTime lastCheckAt) {
        return dslContext.insertInto(LINK)
            .columns(LINK.URL, LINK.LAST_CHECK_AT)
            .values(url.toString(), lastCheckAt)
            .onConflict(LINK.URL)
            .doUpdate()
            .set(LINK.LAST_CHECK_AT, excluded(LINK.LAST_CHECK_AT))
            .returning()
            .fetchOneInto(Link.class);
    }

    @Override
    @Transactional
    public Link remove(URI url) {
        dslContext.deleteFrom(CHAT_LINK)
            .where(CHAT_LINK.LINK_ID.in(select(LINK.ID).from(LINK).where(LINK.URL.eq(url.toString()))))
            .execute();
        return dslContext.deleteFrom(LINK)
            .where(LINK.URL.eq(url.toString()))
            .returning()
            .fetchOneInto(Link.class);
    }

    @Override
    public Link find(URI url) {
        return dslContext.selectFrom(LINK)
            .where(LINK.URL.eq(url.toString()))
            .fetchOneInto(Link.class);
    }

    @Override
    public Link find(Long id) {
        return dslContext.selectFrom(LINK)
            .where(LINK.ID.eq(id))
            .fetchOneInto(Link.class);
    }

    @Override
    public List<Link> findAll() {
        return dslContext.selectFrom(LINK)
            .fetchInto(Link.class);
    }

    @Override
    public List<Link> findAll(OffsetDateTime minLastCheckTime) {
        return dslContext.selectFrom(LINK)
            .where(LINK.LAST_CHECK_AT.le(minLastCheckTime))
            .fetchInto(Link.class);
    }
}
