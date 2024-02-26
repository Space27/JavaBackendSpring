package edu.java.scrapper.repository;

import java.util.List;

public interface LinkStorage {

    boolean add(Long chatID, String link);

    boolean remove(Long chatID, String link);

    boolean addChat(Long chatID);

    boolean deleteChat(Long chatID);

    List<String> get(Long chatID);

    boolean contains(Long chatID);
}
