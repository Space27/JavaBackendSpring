package edu.java.scrapper.domain.dao.jpa;

import edu.java.scrapper.domain.dao.jpa.entity.ChatEntity;
import edu.java.scrapper.domain.dao.jpa.entity.ChatLinkEntity;
import edu.java.scrapper.domain.dao.jpa.entity.LinkEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JpaChatLinkDao extends JpaRepository<ChatLinkEntity, Long> {

    List<ChatLinkEntity> findAllByChat(ChatEntity chat);

    List<ChatLinkEntity> findAllByLink(LinkEntity link);

    void deleteAllByChatId(Long chatId);

    boolean existsByChatAndLink(ChatEntity chat, LinkEntity link);

    Long deleteByChatAndLink(ChatEntity chat, LinkEntity link);
}
