package edu.java.bot.storage;

import java.util.List;

public interface LinkStorage {

    void add(Long chatID, String link);

    void remove(Long chatID, String link);

    void addChat(Long chatID);

    List<String> get(Long chatID);

    boolean contains(Long chatID);
}
