package edu.java.scrapper.api.controllers.exceptions;

import java.net.URI;

public class LinkAlreadyExistsException extends RuntimeException {

    public LinkAlreadyExistsException(String link) {
        super("Ссылка " + link + " уже добавлена");
    }

    public LinkAlreadyExistsException(URI link) {
        this(link.toString());
    }
}
