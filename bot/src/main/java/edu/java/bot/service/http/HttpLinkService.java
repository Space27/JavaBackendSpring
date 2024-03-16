package edu.java.bot.service.http;

import edu.java.bot.service.LinkService;
import edu.java.bot.service.client.scrapper.ResponseErrorException;
import edu.java.bot.service.client.scrapper.ScrapperClient;
import edu.java.bot.service.client.scrapper.response.LinkResponse;
import java.net.URI;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class HttpLinkService implements LinkService {

    private final ScrapperClient scrapperClient;

    private static final String BAD_REQUEST = "400";
    private static final String NOT_FOUND = "404";
    private static final String NOT_ACCEPTABLE = "406";
    private static final String CONFLICT = "409";

    @Override
    public Response addLink(Long chatId, URI url) {
        try {
            scrapperClient.addLink(chatId, url);
            return Response.OK;
        } catch (ResponseErrorException e) {
            return switch (e.getApiErrorResponse().code()) {
                case BAD_REQUEST -> Response.NOT_VALID_REQUEST;
                case NOT_ACCEPTABLE -> Response.CHAT_NOT_EXISTS;
                case CONFLICT -> Response.LINK_ALREADY_TRACKING;
                default -> Response.UNKNOWN;
            };
        } catch (Exception e) {
            log.error("Unexpected exception while Scrapper Client link adding", e);
            return Response.UNKNOWN;
        }
    }

    @Override
    public Response removeLink(Long chatId, URI url) {
        try {
            scrapperClient.removeLink(chatId, url);
            return Response.OK;
        } catch (ResponseErrorException e) {
            return switch (e.getApiErrorResponse().code()) {
                case BAD_REQUEST -> Response.NOT_VALID_REQUEST;
                case NOT_ACCEPTABLE -> Response.CHAT_NOT_EXISTS;
                case NOT_FOUND -> Response.LINK_NOT_TRACKING;
                default -> Response.UNKNOWN;
            };
        } catch (Exception e) {
            log.error("Unexpected exception while Scrapper Client link removing", e);
            return Response.UNKNOWN;
        }
    }

    @Override
    public List<URI> getLinks(Long chatId) {
        try {
            return scrapperClient.getLinks(chatId).links().stream()
                .map(LinkResponse::url)
                .toList();
        } catch (ResponseErrorException e) {
            return null;
        } catch (Exception e) {
            log.error("Unexpected exception while Scrapper Client links getting", e);
            return null;
        }
    }
}
