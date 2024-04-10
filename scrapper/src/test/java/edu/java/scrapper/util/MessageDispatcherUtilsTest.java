package edu.java.scrapper.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.time.OffsetDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import static org.assertj.core.api.Assertions.assertThat;

public class MessageDispatcherUtilsTest {

    @Test
    @DisplayName("Фильтрация сообщений по времени")
    void getMessagesByTime_shouldFilterMessagesByTime() {
        OffsetDateTime base = OffsetDateTime.now().withNano(0);

        Map<String, OffsetDateTime> messages = new LinkedHashMap<>() {
            {
                put("a", base.minusNanos(1));
                put("b", base);
                put("c", base.plusNanos(1));
                put("d", base.minusDays(1));
                put("e", base.plusDays(1));
            }
        };

        String message = MessageDispatcherUtils.getMessagesByTime(messages, base);

        assertThat(message)
            .isEqualTo("c\ne");
    }

    @Test
    @DisplayName("Фильтрация сообщений по времени пустая")
    void getMessagesByTime_shouldReturnNullStringForEmptyMap() {
        OffsetDateTime base = OffsetDateTime.now().withNano(0);

        Map<String, OffsetDateTime> messages = Map.of();

        String message = MessageDispatcherUtils.getMessagesByTime(messages, base);

        assertThat(message)
            .isNull();
    }

    @Test
    @DisplayName("Группировка адресатов")
    void groupMessagesForChats_shouldGroupMessages() {
        OffsetDateTime base = OffsetDateTime.now().withNano(0);

        Map<String, OffsetDateTime> messages = new LinkedHashMap<>() {
            {
                put("a", base.minusNanos(1));
                put("b", base);
                put("c", base.plusNanos(1));
                put("d", base.minusDays(1));
                put("e", base.plusDays(1));
            }
        };
        Map<Long, OffsetDateTime> chats = new LinkedHashMap<>() {
            {
                put(0L, base);
                put(1L, base.minusNanos(1));
                put(2L, base);
                put(3L, base.plusDays(1));
                put(4L, base.minusHours(2));
                put(5L, base.minusSeconds(1));
            }
        };
        Map<String, List<Long>> expected = Map.of(
            "b\nc\ne", List.of(1L),
            "c\ne", List.of(0L, 2L),
            "a\nb\nc\ne", List.of(4L, 5L)
        );

        var groupedMessages = MessageDispatcherUtils.groupMessagesForChats(messages, chats);

        assertThat(groupedMessages)
            .isNotEmpty()
            .hasSize(expected.size())
            .isEqualTo(expected);
    }
}
