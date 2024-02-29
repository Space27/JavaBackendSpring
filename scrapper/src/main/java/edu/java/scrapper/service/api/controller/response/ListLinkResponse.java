package edu.java.scrapper.service.api.controller.response;

import java.util.List;

public record ListLinkResponse(List<LinkResponse> links,
                               Integer size) {
}
