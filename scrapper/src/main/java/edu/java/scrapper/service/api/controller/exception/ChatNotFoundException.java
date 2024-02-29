package edu.java.scrapper.service.api.controller.exception;

public class ChatNotFoundException extends RuntimeException {

    public ChatNotFoundException(Long chatID) {
        super("Чат " + chatID + " не существует");
    }
}
