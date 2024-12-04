package edu.java.scrapper.domain.dao.jpa;

import edu.java.scrapper.domain.dao.jpa.entity.LinkEntity;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaLinkDao extends JpaRepository<LinkEntity, Long> {

    Optional<LinkEntity> findByUrl(String url);

    void deleteByUrl(String url);

    List<LinkEntity> findLinkEntitiesByLastCheckAtLessThanEqual(OffsetDateTime minLastCheckTime);

    boolean existsByUrl(String url);
}
