package edu.java.scrapper.repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Repository;

@Repository
public class InMemoryLinkStorage implements LinkStorage {

    private final Map<Long, List<Link>> userLinks;
    private final Map<String, Long> links;

    public InMemoryLinkStorage() {
        userLinks = new HashMap<>();
        links = new HashMap<>();
    }

    @Override
    public Long add(Long chatID, String link) {
        if (contains(chatID)) {
            Link tmpLink;

            if (links.containsKey(link)) {
                tmpLink = new Link(links.get(link), link);
            } else {
                Long id = links.values().stream()
                    .mapToLong(Long::longValue)
                    .max()
                    .orElse(0L) + 1;

                tmpLink = new Link(id, link);
                links.put(tmpLink.link(), tmpLink.id());
            }

            if (!userLinks.get(chatID).contains(tmpLink)) {
                userLinks.get(chatID).add(tmpLink);
                return tmpLink.id();
            }
        }
        return null;
    }

    @Override
    public Long remove(Long chatID, String link) {
        Link tmpLink = null;

        if (links.containsKey(link)) {
            tmpLink = new Link(links.get(link), link);
        }

        if (tmpLink != null && contains(chatID) && userLinks.get(chatID).contains(tmpLink)) {
            userLinks.get(chatID).remove(tmpLink);
            return tmpLink.id();
        }
        return null;
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
    public List<Link> get(Long chatID) {
        return userLinks.get(chatID);
    }

    @Override
    public boolean contains(Long chatID) {
        return userLinks.containsKey(chatID);
    }
}
