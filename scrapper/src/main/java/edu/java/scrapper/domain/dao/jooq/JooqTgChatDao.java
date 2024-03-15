package edu.java.scrapper.domain.dao.jooq;

import edu.java.scrapper.domain.dao.TgChatDao;
import edu.java.scrapper.domain.dto.Chat;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import static edu.java.scrapper.domain.jooq.Tables.CHAT;
import static edu.java.scrapper.domain.jooq.Tables.CHAT_LINK;

@Repository
@RequiredArgsConstructor
public class JooqTgChatDao implements TgChatDao {

    private final DSLContext dslContext;

    @Override
    public boolean add(Long chatID) {
        return dslContext.insertInto(CHAT)
            .columns(CHAT.ID)
            .values(chatID)
            .onConflictDoNothing()
            .execute() == 1;
    }

    @Override
    @Transactional
    public boolean remove(Long chatID) {
        dslContext.deleteFrom(CHAT_LINK)
            .where(CHAT_LINK.CHAT_ID.eq(chatID))
            .execute();
        return dslContext.deleteFrom(CHAT)
            .where(CHAT.ID.eq(chatID))
            .execute() == 1;
    }

    @Override
    public Chat findById(Long chatID) {
        return dslContext.selectFrom(CHAT)
            .where(CHAT.ID.eq(chatID))
            .fetchOneInto(Chat.class);
    }

    @Override
    public List<Chat> findAll() {
        return dslContext.selectFrom(CHAT)
            .fetchInto(Chat.class);
    }

    @Override
    public List<Long> findAllIds() {
        return dslContext.select(CHAT.ID)
            .from(CHAT)
            .fetchInto(Long.class);
    }
}
