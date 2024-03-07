package edu.java.scrapper.controller.chatApi.exception;

public class ChatNotFoundException extends RuntimeException {

    private static final String ERROR_MESSAGE = "Чат %s не существует";

    public ChatNotFoundException(Long chatID) {
        super(String.format(ERROR_MESSAGE, chatID));
    }
}
