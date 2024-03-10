package edu.java.bot.service;

import edu.java.bot.service.client.scrapperClient.ResponseErrorException;
import edu.java.bot.service.client.scrapperClient.ScrapperClient;
import edu.java.bot.service.client.scrapperClient.response.LinkResponse;
import java.net.URI;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LinkService {

    private final ScrapperClient scrapperClient;

    private static final String BAD_REQUEST = "400";
    private static final String NOT_FOUND = "404";
    private static final String NOT_ACCEPTABLE = "406";
    private static final String CONFLICT = "409";

    Responses addLink(Long chatId, URI url) {
        try {
            scrapperClient.addLink(chatId, url);
            return Responses.OK;
        } catch (ResponseErrorException e) {
            return switch (e.getApiErrorResponse().code()) {
                case BAD_REQUEST -> Responses.NOT_VALID_REQUEST;
                case NOT_ACCEPTABLE -> Responses.CHAT_NOT_EXISTS;
                case CONFLICT -> Responses.LINK_ALREADY_TRACKING;
                default -> Responses.UNKNOWN;
            };
        }
    }

    Responses removeLink(Long chatId, URI url) {
        try {
            scrapperClient.removeLink(chatId, url);
            return Responses.OK;
        } catch (ResponseErrorException e) {
            return switch (e.getApiErrorResponse().code()) {
                case BAD_REQUEST -> Responses.NOT_VALID_REQUEST;
                case NOT_ACCEPTABLE -> Responses.CHAT_NOT_EXISTS;
                case NOT_FOUND -> Responses.LINK_NOT_TRACKING;
                default -> Responses.UNKNOWN;
            };
        }
    }

    List<URI> getLinks(Long chatId) {
        try {
            return scrapperClient.getLinks(chatId).links().stream()
                .map(LinkResponse::url)
                .toList();
        } catch (ResponseErrorException e) {
            return null;
        }
    }

    public enum Responses {
        OK,
        CHAT_NOT_EXISTS,
        LINK_ALREADY_TRACKING,
        LINK_NOT_TRACKING,
        NOT_VALID_REQUEST,
        UNKNOWN
    }
}
