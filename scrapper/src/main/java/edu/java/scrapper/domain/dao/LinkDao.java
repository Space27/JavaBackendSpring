package edu.java.scrapper.domain.dao;

import edu.java.scrapper.domain.dto.Link;
import java.net.URI;
import java.time.OffsetDateTime;
import java.util.List;

public interface LinkDao {

    Link add(URI url);

    Link add(URI url, OffsetDateTime lastCheckAt);

    Link remove(URI url);

    Link find(URI url);

    Link find(Long id);

    List<Link> findAll();

    List<Link> findAll(OffsetDateTime minLastCheckTime);
}
