package edu.java.scrapper.api.controllers.exceptions;

public class ChatNotFoundException extends RuntimeException {

    public ChatNotFoundException(Long chatID) {
        super("Чат " + chatID + " не существует");
    }
}
