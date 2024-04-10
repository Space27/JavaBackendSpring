package edu.java.bot.service.client.scrapper.response;

import java.util.List;

public record ListLinkResponse(List<LinkResponse> links,
                               Integer size) {
}
