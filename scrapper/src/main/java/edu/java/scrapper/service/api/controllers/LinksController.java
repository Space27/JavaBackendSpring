package edu.java.scrapper.service.api.controllers;

import edu.java.scrapper.repository.LinkStorage;
import edu.java.scrapper.service.api.controllers.exceptions.ChatNotExistsException;
import edu.java.scrapper.service.api.controllers.exceptions.LinkAlreadyExistsException;
import edu.java.scrapper.service.api.controllers.exceptions.LinkNotFoundException;
import edu.java.scrapper.service.api.schemas.AddLinkRequest;
import edu.java.scrapper.service.api.schemas.ApiErrorResponse;
import edu.java.scrapper.service.api.schemas.LinkResponse;
import edu.java.scrapper.service.api.schemas.ListLinkResponse;
import edu.java.scrapper.service.api.schemas.RemoveLinkRequest;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.net.URI;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/links")
@RequiredArgsConstructor
public class LinksController {

    private final LinkStorage linkStorage;

    @GetMapping
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200",
                     description = "Ссылки успешно получены",
                     content = @Content(mediaType = "application/json",
                                        schema = @Schema(implementation = ListLinkResponse.class))),
        @ApiResponse(responseCode = "400",
                     description = "Некорректные параметры запроса",
                     content = @Content(mediaType = "application/json",
                                        schema = @Schema(implementation = ApiErrorResponse.class))),
        @ApiResponse(responseCode = "406",
                     description = "Нельзя получить доступ к чату",
                     content = @Content(mediaType = "application/json",
                                        schema = @Schema(implementation = ApiErrorResponse.class)))})
    @ResponseStatus(HttpStatus.OK)
    public ListLinkResponse getLinks(@RequestHeader(value = "Tg-Chat-Id") @NotNull @Positive Long id) {
        if (!linkStorage.contains(id)) {
            throw new ChatNotExistsException(id);
        }

        List<String> links = linkStorage.get(id);

        List<LinkResponse> linkResponses = links.stream()
            .map(link -> new LinkResponse(id, URI.create(link)))
            .toList();

        return new ListLinkResponse(linkResponses, linkResponses.size());
    }

    @PostMapping
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200",
                     description = "Ссылка успешно добавлена",
                     content = @Content(mediaType = "application/json",
                                        schema = @Schema(implementation = LinkResponse.class))),
        @ApiResponse(responseCode = "400",
                     description = "Некорректные параметры запроса",
                     content = @Content(mediaType = "application/json",
                                        schema = @Schema(implementation = ApiErrorResponse.class))),
        @ApiResponse(responseCode = "406",
                     description = "Нельзя получить доступ к чату",
                     content = @Content(mediaType = "application/json",
                                        schema = @Schema(implementation = ApiErrorResponse.class))),
        @ApiResponse(responseCode = "409",
                     description = "Ссылка уже добавлена",
                     content = @Content(mediaType = "application/json",
                                        schema = @Schema(implementation = ApiErrorResponse.class)))})
    @ResponseStatus(HttpStatus.OK)
    public LinkResponse addLink(
        @RequestHeader(value = "Tg-Chat-Id") @NotNull @Positive Long id,
        @RequestBody @NotNull @Valid AddLinkRequest addLinkRequest
    ) {
        if (!linkStorage.contains(id)) {
            throw new ChatNotExistsException(id);
        }

        if (!linkStorage.add(id, addLinkRequest.link().toString())) {
            throw new LinkAlreadyExistsException(addLinkRequest.link());
        }

        return new LinkResponse(id, addLinkRequest.link());
    }

    @DeleteMapping
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200",
                     description = "Ссылка успешно убрана",
                     content = @Content(mediaType = "application/json",
                                        schema = @Schema(implementation = LinkResponse.class))),
        @ApiResponse(responseCode = "400",
                     description = "Некорректные параметры запроса",
                     content = @Content(mediaType = "application/json",
                                        schema = @Schema(implementation = ApiErrorResponse.class))
        ),
        @ApiResponse(responseCode = "404",
                     description = "Ссылка не найдена",
                     content = @Content(mediaType = "application/json",
                                        schema = @Schema(implementation = ApiErrorResponse.class))),
        @ApiResponse(responseCode = "406",
                     description = "Нельзя получить доступ к чату",
                     content = @Content(mediaType = "application/json",
                                        schema = @Schema(implementation = ApiErrorResponse.class)))})
    @ResponseStatus(HttpStatus.OK)
    public LinkResponse deleteLink(
        @RequestHeader(value = "Tg-Chat-Id") @NotNull @Positive Long id,
        @RequestBody @NotNull @Valid RemoveLinkRequest removeLinkRequest
    ) {
        if (!linkStorage.contains(id)) {
            throw new ChatNotExistsException(id);
        }

        if (linkStorage.remove(id, removeLinkRequest.link().toString())) {
            return new LinkResponse(id, removeLinkRequest.link());
        } else {
            throw new LinkNotFoundException(removeLinkRequest.link());
        }
    }
}
