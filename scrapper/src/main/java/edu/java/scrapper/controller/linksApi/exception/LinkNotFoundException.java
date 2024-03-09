package edu.java.scrapper.controller.linksApi.exception;

import java.net.URI;

public class LinkNotFoundException extends RuntimeException {

    private static final String ERROR_MESSAGE = "Ссылка %s не найдена";

    public LinkNotFoundException(String link) {
        super(String.format(ERROR_MESSAGE, link));
    }

    public LinkNotFoundException(URI link) {
        this(link.toString());
    }
}
