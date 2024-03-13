package edu.java.scrapper.domain.dao.jdbc;

import edu.java.scrapper.domain.dao.ChatLinkDao;
import edu.java.scrapper.domain.dto.Chat;
import edu.java.scrapper.domain.dto.ChatLink;
import edu.java.scrapper.domain.dto.Link;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class JdbcChatLinkDao implements ChatLinkDao {

    private static final String ADD_QUERY =
        "INSERT INTO chat_link (chat_id, link_id) VALUES (?, ?) ON CONFLICT DO NOTHING";
    private static final String REMOVE_QUERY =
        "DELETE FROM chat_link WHERE chat_id = ? AND link_id = ?";
    private static final String SELECT_ALL_QUERY =
        "SELECT * FROM chat_link";
    private static final String SELECT_ALL_BY_CHAT_QUERY =
        "SELECT * FROM chat_link WHERE chat_id = ?";
    private static final String SELECT_ALL_BY_LINK_QUERY =
        "SELECT * FROM chat_link WHERE link_id = ?";
    private static final String SELECT_LINKS_BY_CHAT_QUERY =
        "SELECT l.* FROM link l "
            + "JOIN chat_link ON l.id = link_id "
            + "WHERE chat_id = ?";
    private static final String SELECT_CHATS_BY_LINK_QUERY =
        "SELECT c.* FROM chat c "
            + "JOIN chat_link ON c.id = chat_id "
            + "WHERE link_id = ?";

    private final JdbcClient jdbcClient;

    @Override
    public boolean add(Long chatID, Long linkID) {
        try {
            return jdbcClient.sql(ADD_QUERY)
                .param(chatID)
                .param(linkID)
                .update() == 1;
        } catch (DataIntegrityViolationException e) {
            return false;
        }
    }

    @Override
    public boolean remove(Long chatID, Long linkID) {
        return jdbcClient.sql(REMOVE_QUERY)
            .param(chatID)
            .param(linkID)
            .update() == 1;
    }

    @Override
    public List<ChatLink> findAll() {
        return jdbcClient.sql(SELECT_ALL_QUERY)
            .query(ChatLink.class)
            .list();
    }

    @Override
    public List<ChatLink> findAllByChat(Long chatID) {
        return jdbcClient.sql(SELECT_ALL_BY_CHAT_QUERY)
            .param(chatID)
            .query(ChatLink.class)
            .list();
    }

    @Override
    public List<ChatLink> findAllByLink(Long linkID) {
        return jdbcClient.sql(SELECT_ALL_BY_LINK_QUERY)
            .param(linkID)
            .query(ChatLink.class)
            .list();
    }

    @Override
    public List<Link> findLinksByChat(Long chatID) {
        return jdbcClient.sql(SELECT_LINKS_BY_CHAT_QUERY)
            .param(chatID)
            .query(Link.class)
            .list();
    }

    @Override
    public List<Chat> findChatsByLink(Long linkID) {
        return jdbcClient.sql(SELECT_CHATS_BY_LINK_QUERY)
            .param(linkID)
            .query(Chat.class)
            .list();
    }
}
