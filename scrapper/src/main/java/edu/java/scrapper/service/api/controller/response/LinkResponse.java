package edu.java.scrapper.service.api.controller.response;

import java.net.URI;

public record LinkResponse(Long id,
                           URI url) {
}
