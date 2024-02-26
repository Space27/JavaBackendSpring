package edu.java.scrapper.repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Repository;

@Repository
public class InMemoryLinkStorage implements LinkStorage {

    private final Map<Long, List<String>> userLinks;

    public InMemoryLinkStorage() {
        userLinks = new HashMap<>();
    }

    @Override
    public boolean add(Long chatID, String link) {
        if (contains(chatID)) {
            if (!userLinks.get(chatID).contains(link)) {
                userLinks.get(chatID).add(link);
                return true;
            }
        } else {
            userLinks.put(chatID, new ArrayList<>(List.of(link)));
        }
        return false;
    }

    @Override
    public boolean remove(Long chatID, String link) {
        if (contains(chatID) && userLinks.get(chatID).contains(link)) {
            userLinks.get(chatID).remove(link);
            return true;
        }
        return false;
    }

    @Override
    public boolean addChat(Long chatID) {
        if (!contains(chatID)) {
            userLinks.put(chatID, new ArrayList<>());
            return true;
        }
        return false;
    }

    @Override
    public boolean deleteChat(Long chatID) {
        if (contains(chatID)) {
            userLinks.remove(chatID);
            return true;
        }
        return false;
    }

    @Override
    public List<String> get(Long chatID) {
        return userLinks.get(chatID);
    }

    @Override
    public boolean contains(Long chatID) {
        return userLinks.containsKey(chatID);
    }
}