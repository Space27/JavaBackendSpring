package edu.java.scrapper.domain.dao.jdbc;

import edu.java.scrapper.domain.dao.LinkDao;
import edu.java.scrapper.domain.dto.Link;
import java.net.URI;
import java.time.OffsetDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@RequiredArgsConstructor
public class JdbcLinkDao implements LinkDao {

    private static final String ADD_BY_URL_QUERY =
        "INSERT INTO link (url) VALUES (?) "
            + "ON CONFLICT (url) DO UPDATE SET url = excluded.url RETURNING *";
    private static final String ADD_BY_URL_AND_CHECK_TIME_QUERY =
        "INSERT INTO link (url, last_check_at) VALUES (?, ?) "
            + "ON CONFLICT (url) DO UPDATE SET last_check_at = excluded.last_check_at RETURNING *";
    private static final String DELETE_CHAT_LINKS_BY_URL_QUERY =
        "DELETE FROM chat_link WHERE link_id IN (SELECT id from link WHERE url = ?)";
    private static final String DELETE_LINK_BY_URL_QUERY =
        "DELETE FROM link WHERE url = ? RETURNING *";
    private static final String SELECT_BY_URL_QUERY =
        "SELECT * FROM link WHERE url = ?";
    private static final String SELECT_BY_ID_QUERY =
        "SELECT * FROM link WHERE id = ?";
    private static final String SELECT_QUERY =
        "SELECT * FROM link";
    private static final String SELECT_BY_TIME_QUERY =
        "SELECT * FROM link WHERE last_check_at <= ?";

    private final JdbcClient jdbcClient;

    @Override
    public Link add(URI url) {
        return jdbcClient.sql(ADD_BY_URL_QUERY)
            .param(url.toString())
            .query(Link.class)
            .optional().orElse(null);
    }

    @Override
    public Link add(URI url, OffsetDateTime lastCheckAt) {
        return jdbcClient.sql(ADD_BY_URL_AND_CHECK_TIME_QUERY)
            .param(url.toString())
            .param(lastCheckAt)
            .query(Link.class)
            .optional().orElse(null);
    }

    @Override
    @Transactional
    public Link remove(URI url) {
        jdbcClient.sql(DELETE_CHAT_LINKS_BY_URL_QUERY)
            .param(url.toString())
            .update();
        return jdbcClient.sql(DELETE_LINK_BY_URL_QUERY)
            .param(url.toString())
            .query(Link.class)
            .optional().orElse(null);
    }

    @Override
    public Link find(URI url) {
        return jdbcClient.sql(SELECT_BY_URL_QUERY)
            .param(url.toString())
            .query(Link.class)
            .optional().orElse(null);
    }

    @Override
    public Link find(Long id) {
        return jdbcClient.sql(SELECT_BY_ID_QUERY)
            .param(id)
            .query(Link.class)
            .optional().orElse(null);
    }

    @Override
    public List<Link> findAll() {
        return jdbcClient.sql(SELECT_QUERY)
            .query(Link.class)
            .list();
    }

    @Override
    public List<Link> findAll(OffsetDateTime minLastCheckTime) {
        return jdbcClient.sql(SELECT_BY_TIME_QUERY)
            .param(minLastCheckTime)
            .query(Link.class)
            .list();
    }
}
