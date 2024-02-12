package edu.java.bot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Repository;

@Repository
public class LinkStorage {

    private final Map<Long, List<String>> userLinks;

    public LinkStorage() {
        userLinks = new HashMap<>();
    }

    public void add(Long chatID, String link) {
        if (userLinks.containsKey(chatID)) {
            userLinks.get(chatID).add(link);
        } else {
            userLinks.put(chatID, new ArrayList<>(List.of(link)));
        }
    }

    public void remove(Long chatID, String link) {
        if (userLinks.containsKey(chatID)) {
            userLinks.get(chatID).remove(link);
        }
    }

    public void addChat(Long chatID) {
        if (!userLinks.containsKey(chatID)) {
            userLinks.put(chatID, new ArrayList<>());
        }
    }

    public List<String> get(Long chatID) {
        return userLinks.get(chatID);
    }
}
