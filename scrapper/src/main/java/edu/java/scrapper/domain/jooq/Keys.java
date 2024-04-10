/*
 * This file is generated by jOOQ.
 */

package edu.java.scrapper.domain.jooq;

import edu.java.scrapper.domain.jooq.tables.Chat;
import edu.java.scrapper.domain.jooq.tables.ChatLink;
import edu.java.scrapper.domain.jooq.tables.Link;
import javax.annotation.processing.Generated;
import org.jooq.ForeignKey;
import org.jooq.Record;
import org.jooq.TableField;
import org.jooq.UniqueKey;
import org.jooq.impl.DSL;
import org.jooq.impl.Internal;

/**
 * A class modelling foreign key relationships and constraints of tables in the
 * default schema.
 */
@Generated(
    value = {
        "https://www.jooq.org",
        "jOOQ version:3.18.9"
    },
    comments = "This class is generated by jOOQ"
)
@SuppressWarnings({"all", "unchecked", "rawtypes", "this-escape"})
public class Keys {

    // -------------------------------------------------------------------------
    // UNIQUE and PRIMARY KEY definitions
    // -------------------------------------------------------------------------

    public static final UniqueKey<Record> CONSTRAINT_1 =
        Internal.createUniqueKey(Chat.CHAT, DSL.name("CONSTRAINT_1"), new TableField[] {Chat.CHAT.ID}, true);
    public static final UniqueKey<Record> CONSTRAINT_868 = Internal.createUniqueKey(ChatLink.CHAT_LINK,
        DSL.name("CONSTRAINT_868"),
        new TableField[] {ChatLink.CHAT_LINK.ID},
        true
    );
    public static final UniqueKey<Record> CONSTRAINT_8682 = Internal.createUniqueKey(ChatLink.CHAT_LINK,
        DSL.name("CONSTRAINT_8682"),
        new TableField[] {ChatLink.CHAT_LINK.LINK_ID, ChatLink.CHAT_LINK.CHAT_ID},
        true
    );
    public static final UniqueKey<Record> CONSTRAINT_2 =
        Internal.createUniqueKey(Link.LINK, DSL.name("CONSTRAINT_2"), new TableField[] {Link.LINK.ID}, true);
    public static final UniqueKey<Record> CONSTRAINT_23 =
        Internal.createUniqueKey(Link.LINK, DSL.name("CONSTRAINT_23"), new TableField[] {Link.LINK.URL}, true);

    // -------------------------------------------------------------------------
    // FOREIGN KEY definitions
    // -------------------------------------------------------------------------

    public static final ForeignKey<Record, Record> CONSTRAINT_8 = Internal.createForeignKey(ChatLink.CHAT_LINK,
        DSL.name("CONSTRAINT_8"),
        new TableField[] {ChatLink.CHAT_LINK.LINK_ID},
        Keys.CONSTRAINT_2,
        new TableField[] {Link.LINK.ID},
        true
    );
    public static final ForeignKey<Record, Record> CONSTRAINT_86 = Internal.createForeignKey(ChatLink.CHAT_LINK,
        DSL.name("CONSTRAINT_86"),
        new TableField[] {ChatLink.CHAT_LINK.CHAT_ID},
        Keys.CONSTRAINT_1,
        new TableField[] {Chat.CHAT.ID},
        true
    );
}
