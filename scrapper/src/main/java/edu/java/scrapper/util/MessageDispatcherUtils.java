package edu.java.scrapper.util;

import java.time.OffsetDateTime;
import java.util.AbstractMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.experimental.UtilityClass;

@UtilityClass
public class MessageDispatcherUtils {

    public Map<String, List<Long>> groupMessagesForChats(
        Map<String, OffsetDateTime> messages,
        Map<Long, OffsetDateTime> chats
    ) {
        return chats.entrySet().stream()
            .map(e -> new AbstractMap.SimpleEntry<>(getMessagesByTime(messages, e.getValue()), e.getKey()))
            .filter(e -> e.getKey() != null)
            .collect(Collectors.groupingBy(
                Map.Entry::getKey,
                Collectors.mapping(Map.Entry::getValue, Collectors.toList()
                )
            ));
    }

    public String getMessagesByTime(Map<String, OffsetDateTime> messages, OffsetDateTime minTime) {
        String message = messages.entrySet().stream()
            .filter(entry -> entry.getValue().isAfter(minTime))
            .map(Map.Entry::getKey)
            .collect(Collectors.joining("\n"));

        return message.isEmpty() ? null : message;
    }
}
