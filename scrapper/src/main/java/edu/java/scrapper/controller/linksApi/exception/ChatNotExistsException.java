package edu.java.scrapper.controller.linksApi.exception;

public class ChatNotExistsException extends RuntimeException {

    private static final String ERROR_MESSAGE = "Чат %s не существует";

    public ChatNotExistsException(Long chatID) {
        super(String.format(ERROR_MESSAGE, chatID));
    }
}
