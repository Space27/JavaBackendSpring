package edu.java.scrapper.domain.dao.jpa;

import edu.java.scrapper.domain.dao.jpa.entity.ChatEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JpaTgChatDao extends JpaRepository<ChatEntity, Long> {
}
