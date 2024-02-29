package edu.java.scrapper.service.api.controller.linksApi;

import edu.java.scrapper.service.api.controller.request.AddLinkRequest;
import edu.java.scrapper.service.api.controller.request.RemoveLinkRequest;
import edu.java.scrapper.service.api.controller.response.ApiErrorResponse;
import edu.java.scrapper.service.api.controller.response.LinkResponse;
import edu.java.scrapper.service.api.controller.response.ListLinkResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

public interface LinksApi {

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
    ResponseEntity<ListLinkResponse> getLinks(@RequestHeader(value = "Tg-Chat-Id") @NotNull @Positive Long id);

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
    ResponseEntity<LinkResponse> addLink(
        @RequestHeader(value = "Tg-Chat-Id") @NotNull @Positive Long id,
        @RequestBody @NotNull @Valid AddLinkRequest addLinkRequest
    );

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
    ResponseEntity<LinkResponse> deleteLink(
        @RequestHeader(value = "Tg-Chat-Id") @NotNull @Positive Long id,
        @RequestBody @NotNull @Valid RemoveLinkRequest removeLinkRequest
    );
}
