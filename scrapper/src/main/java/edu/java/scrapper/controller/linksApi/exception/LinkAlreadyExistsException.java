package edu.java.scrapper.controller.linksApi.exception;

import java.net.URI;

public class LinkAlreadyExistsException extends RuntimeException {

    private static final String ERROR_MESSAGE = "Ссылка %s уже добавлена";

    public LinkAlreadyExistsException(String link) {
        super(String.format(ERROR_MESSAGE, link));
    }

    public LinkAlreadyExistsException(URI link) {
        this(link.toString());
    }
}
