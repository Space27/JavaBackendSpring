package edu.java.scrapper.domain.dao.jpa;

import edu.java.scrapper.domain.dao.jpa.entity.ChatEntity;
import edu.java.scrapper.domain.dao.jpa.entity.ChatLinkEntity;
import edu.java.scrapper.domain.dao.jpa.entity.LinkEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaChatLinkDao extends JpaRepository<ChatLinkEntity, Long> {

    List<ChatLinkEntity> findAllByChat(ChatEntity chat);

    List<ChatLinkEntity> findAllByLink(LinkEntity link);

    void deleteAllByChatId(Long chatId);

    boolean existsByChatAndLink(ChatEntity chat, LinkEntity link);

    Long deleteByChatAndLink(ChatEntity chat, LinkEntity link);
}
