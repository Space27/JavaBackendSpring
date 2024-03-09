package edu.java.scrapper.controller.response;

import java.util.List;

public record ListLinkResponse(List<LinkResponse> links,
                               Integer size) {
}
