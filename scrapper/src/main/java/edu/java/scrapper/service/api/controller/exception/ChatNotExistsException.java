package edu.java.scrapper.service.api.controller.exception;

public class ChatNotExistsException extends RuntimeException {

    public ChatNotExistsException(Long chatID) {
        super("Чат " + chatID + " не существует");
    }
}
