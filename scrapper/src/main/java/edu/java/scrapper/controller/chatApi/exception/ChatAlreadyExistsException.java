package edu.java.scrapper.controller.chatApi.exception;

public class ChatAlreadyExistsException extends RuntimeException {

    private static final String ERROR_MESSAGE = "Чат %s уже существует";

    public ChatAlreadyExistsException(Long chatID) {
        super(String.format(ERROR_MESSAGE, chatID));
    }
}
