package edu.java.scrapper.domain.dao.jooq;

import edu.java.scrapper.domain.dao.ChatLinkDao;
import edu.java.scrapper.domain.dto.Chat;
import edu.java.scrapper.domain.dto.ChatLink;
import edu.java.scrapper.domain.dto.Link;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.dao.DataIntegrityViolationException;
import static edu.java.scrapper.domain.jooq.Tables.CHAT;
import static edu.java.scrapper.domain.jooq.Tables.CHAT_LINK;
import static edu.java.scrapper.domain.jooq.Tables.LINK;

@RequiredArgsConstructor
public class JooqChatLinkDao implements ChatLinkDao {

    private final DSLContext dslContext;

    @Override
    public boolean add(Long chatID, Long linkID) {
        try {
            return dslContext.insertInto(CHAT_LINK)
                .columns(CHAT_LINK.CHAT_ID, CHAT_LINK.LINK_ID)
                .values(chatID, linkID)
                .onConflictDoNothing()
                .execute() == 1;
        } catch (DataIntegrityViolationException e) {
            return false;
        }
    }

    @Override
    public boolean remove(Long chatID, Long linkID) {
        return dslContext.deleteFrom(CHAT_LINK)
            .where(CHAT_LINK.CHAT_ID.eq(chatID)).and(CHAT_LINK.LINK_ID.eq(linkID))
            .execute() == 1;
    }

    @Override
    public List<ChatLink> findAll() {
        return dslContext.selectFrom(CHAT_LINK)
            .fetchInto(ChatLink.class);
    }

    @Override
    public List<ChatLink> findAllByChat(Long chatID) {
        return dslContext.selectFrom(CHAT_LINK)
            .where(CHAT_LINK.CHAT_ID.eq(chatID))
            .fetchInto(ChatLink.class);
    }

    @Override
    public List<ChatLink> findAllByLink(Long linkID) {
        return dslContext.selectFrom(CHAT_LINK)
            .where(CHAT_LINK.LINK_ID.eq(linkID))
            .fetchInto(ChatLink.class);
    }

    @Override
    public List<Link> findLinksByChat(Long chatID) {
        return dslContext.select(LINK.fields())
            .from(LINK)
            .join(CHAT_LINK).on(LINK.ID.eq(CHAT_LINK.LINK_ID))
            .where(CHAT_LINK.CHAT_ID.eq(chatID))
            .fetchInto(Link.class);
    }

    @Override
    public List<Chat> findChatsByLink(Long linkID) {
        return dslContext.select(CHAT.fields())
            .from(CHAT)
            .join(CHAT_LINK).on(CHAT.ID.eq(CHAT_LINK.CHAT_ID))
            .where(CHAT_LINK.LINK_ID.eq(linkID))
            .fetchInto(Chat.class);
    }
}
