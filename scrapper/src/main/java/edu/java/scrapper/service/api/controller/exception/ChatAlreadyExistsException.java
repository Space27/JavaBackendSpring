package edu.java.scrapper.service.api.controller.exception;

public class ChatAlreadyExistsException extends RuntimeException {

    public ChatAlreadyExistsException(Long chatID) {
        super("Чат " + chatID + " уже существует");
    }
}
