package edu.java.scrapper.domain.chatLink;

import edu.java.scrapper.domain.link.Link;
import edu.java.scrapper.domain.tgChat.Chat;
import java.util.List;

public interface ChatLinkDao {

    boolean add(Long chatID, Long linkID);

    boolean remove(Long chatID, Long linkID);

    List<ChatLink> findAll();

    List<Link> findLinksByChat(Long chatID);

    List<Chat> findChatsByLink(Long linkID);
}
