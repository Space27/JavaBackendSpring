package edu.java.scrapper.service.api.controllers.exceptions;

import java.net.URI;

public class LinkNotFoundException extends RuntimeException {

    public LinkNotFoundException(String link) {
        super("Ссылка " + link + " не найдена");
    }

    public LinkNotFoundException(URI link) {
        this(link.toString());
    }
}
