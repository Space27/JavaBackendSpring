package edu.java.scrapper.domain.chatLink.jdbcImpl;

import edu.java.scrapper.domain.chatLink.ChatLink;
import edu.java.scrapper.domain.chatLink.ChatLinkDao;
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
        "DELETE FROM chat_link WHERE chat_id = ? and link_id = ?";
    private static final String SELECT_ALL_QUERY =
        "SELECT * FROM chat_link";
    private static final String SELECT_BY_CHAT_QUERY =
        "SELECT * FROM chat_link WHERE chat_id = ?";
    private static final String SELECT_BY_LINK_QUERY =
        "SELECT * FROM chat_link WHERE link_id = ?";

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
        return jdbcClient.sql(SELECT_BY_CHAT_QUERY)
            .param(chatID)
            .query(ChatLink.class)
            .list();
    }

    @Override
    public List<ChatLink> findAllByLink(Long linkID) {
        return jdbcClient.sql(SELECT_BY_LINK_QUERY)
            .param(linkID)
            .query(ChatLink.class)
            .list();
    }
}
