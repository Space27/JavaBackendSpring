package edu.java.scrapper.service.api.controllers.exceptions;

public class ChatNotFoundException extends RuntimeException {

    public ChatNotFoundException(Long chatID) {
        super("Чат " + chatID + " не существует");
    }
}
