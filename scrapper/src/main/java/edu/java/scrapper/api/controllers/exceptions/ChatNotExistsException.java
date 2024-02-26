package edu.java.scrapper.api.controllers.exceptions;

public class ChatNotExistsException extends RuntimeException {

    public ChatNotExistsException(Long chatID) {
        super("Чат " + chatID + " не существует");
    }
}
