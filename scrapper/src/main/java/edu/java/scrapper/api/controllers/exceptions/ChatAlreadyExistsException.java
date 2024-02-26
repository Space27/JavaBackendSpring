package edu.java.scrapper.api.controllers.exceptions;

public class ChatAlreadyExistsException extends RuntimeException {

    public ChatAlreadyExistsException(Long chatID) {
        super("Чат " + chatID + " уже существует");
    }
}
