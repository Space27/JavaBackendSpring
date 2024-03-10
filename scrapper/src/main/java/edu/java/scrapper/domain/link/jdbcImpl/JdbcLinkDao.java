package edu.java.scrapper.domain.link.jdbcImpl;

import edu.java.scrapper.domain.link.Link;
import edu.java.scrapper.domain.link.LinkDao;
import java.net.URI;
import java.time.OffsetDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class JdbcLinkDao implements LinkDao {

    private static final String ADD_BY_URL_QUERY =
        "INSERT INTO link (url) VALUES (?) "
            + "ON CONFLICT (url) DO UPDATE SET url = excluded.url RETURNING *";
    private static final String ADD_BY_URL_AND_CHECK_TIME_QUERY =
        "INSERT INTO link (url, last_check_at) VALUES (?, ?) "
            + "ON CONFLICT (url) DO UPDATE SET last_check_at = excluded.last_check_at RETURNING *";
    private static final String REMOVE_BY_URL_QUERY =
        "DELETE FROM link WHERE url = ? RETURNING *";
    private static final String SELECT_BY_URL_QUERY =
        "SELECT * FROM link WHERE url = ?";
    private static final String SELECT_BY_ID_QUERY =
        "SELECT * FROM link WHERE id = ?";
    private static final String SELECT_QUERY =
        "SELECT * FROM link";

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
    public Link remove(URI url) {
        return jdbcClient.sql(REMOVE_BY_URL_QUERY)
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
}
