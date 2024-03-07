package edu.java.scrapper.repository;

import java.util.List;

public interface LinkRepository {

    Long add(Long chatID, String link);

    Long remove(Long chatID, String link);

    boolean addChat(Long chatID);

    boolean deleteChat(Long chatID);

    List<Link> get(Long chatID);

    boolean contains(Long chatID);

    record Link(Long id, String link) {
    }
}
