package edu.java.scrapper.service.api.controllers;

import edu.java.scrapper.repository.LinkStorage;
import edu.java.scrapper.service.api.controllers.exceptions.ChatAlreadyExistsException;
import edu.java.scrapper.service.api.controllers.exceptions.ChatNotFoundException;
import edu.java.scrapper.service.api.schemas.ApiErrorResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/tg-chat")
@RequiredArgsConstructor
public class ChatController {

    private final LinkStorage linkStorage;

    @PostMapping("/{id}")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Чат зарегистрирован"),
        @ApiResponse(responseCode = "400",
                     description = "Некорректные параметры запроса",
                     content = @Content(mediaType = "application/json",
                                        schema = @Schema(implementation = ApiErrorResponse.class))),
        @ApiResponse(responseCode = "409",
                     description = "Чат уже зарегистрирован",
                     content = @Content(mediaType = "application/json",
                                        schema = @Schema(implementation = ApiErrorResponse.class)))})
    @ResponseStatus(HttpStatus.OK)
    public void addChat(@PathVariable @NotNull @Positive Long id) {
        if (!linkStorage.contains(id)) {
            linkStorage.addChat(id);
        } else {
            throw new ChatAlreadyExistsException(id);
        }
    }

    @DeleteMapping("/{id}")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Чат успешно удалён"),
        @ApiResponse(responseCode = "400",
                     description = "Некорректные параметры запроса",
                     content = @Content(mediaType = "application/json",
                                        schema = @Schema(implementation = ApiErrorResponse.class))),
        @ApiResponse(responseCode = "404",
                     description = "Чат не существует",
                     content = @Content(mediaType = "application/json",
                                        schema = @Schema(implementation = ApiErrorResponse.class)))})
    @ResponseStatus(HttpStatus.OK)
    public void deleteChat(@PathVariable @NotNull @Positive Long id) {
        if (linkStorage.contains(id)) {
            linkStorage.deleteChat(id);
        } else {
            throw new ChatNotFoundException(id);
        }
    }
}
