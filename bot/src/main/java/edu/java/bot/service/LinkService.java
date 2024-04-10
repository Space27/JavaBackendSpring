package edu.java.bot.service;

import java.net.URI;
import java.util.List;

public interface LinkService {

    Response addLink(Long chatId, URI url);

    Response removeLink(Long chatId, URI url);

    List<URI> getLinks(Long chatId);

    enum Response {
        OK,
        CHAT_NOT_EXISTS,
        LINK_ALREADY_TRACKING,
        LINK_NOT_TRACKING,
        NOT_VALID_REQUEST,
        UNKNOWN
    }
}
