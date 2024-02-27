package edu.java.scrapper.service.api.controllers.exceptions;

public class ChatNotExistsException extends RuntimeException {

    public ChatNotExistsException(Long chatID) {
        super("Чат " + chatID + " не существует");
    }
}
