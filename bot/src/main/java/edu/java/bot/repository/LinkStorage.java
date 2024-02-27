package edu.java.bot.repository;

import java.util.List;

public interface LinkStorage {

    void add(Long chatID, String link);

    void remove(Long chatID, String link);

    void addChat(Long chatID);

    List<String> get(Long chatID);

    boolean contains(Long chatID);
}
