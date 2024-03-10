package edu.java.scrapper.domain.chatLink;

import java.util.List;

public interface ChatLinkDao {

    boolean add(Long chatID, Long linkID);

    boolean remove(Long chatID, Long linkID);

    List<ChatLink> findAll();

    List<ChatLink> findAllByChat(Long chatID);

    List<ChatLink> findAllByLink(Long linkID);
}
