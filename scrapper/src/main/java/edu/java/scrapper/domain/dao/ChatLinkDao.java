package edu.java.scrapper.domain.dao;

import edu.java.scrapper.domain.dto.Chat;
import edu.java.scrapper.domain.dto.ChatLink;
import edu.java.scrapper.domain.dto.Link;
import java.util.List;

public interface ChatLinkDao {

    boolean add(Long chatID, Long linkID);

    boolean remove(Long chatID, Long linkID);

    List<ChatLink> findAll();

    List<ChatLink> findAllByChat(Long chatID);

    List<ChatLink> findAllByLink(Long linkID);

    List<Link> findLinksByChat(Long chatID);

    List<Chat> findChatsByLink(Long linkID);
}
