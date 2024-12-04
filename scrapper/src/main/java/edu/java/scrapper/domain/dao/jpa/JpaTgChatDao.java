package edu.java.scrapper.domain.dao.jpa;

import edu.java.scrapper.domain.dao.jpa.entity.ChatEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaTgChatDao extends JpaRepository<ChatEntity, Long> {
}
