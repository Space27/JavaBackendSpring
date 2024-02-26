package edu.java.bot.service.api.controllers;

import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.service.api.schemas.ApiErrorResponse;
import edu.java.bot.service.api.schemas.LinkUpdate;
import edu.java.bot.telegram.IBot;
import edu.java.bot.util.UpdateParser;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/updates")
@RequiredArgsConstructor
public class UpdateController {

    private final IBot bot;

    @PostMapping
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Обновление обработано"),
        @ApiResponse(responseCode = "400",
                     description = "Некорректные параметры запроса",
                     content = @Content(mediaType = "application/json",
                                        schema = @Schema(implementation = ApiErrorResponse.class)))})
    @ResponseStatus(HttpStatus.OK)
    public void update(@RequestBody @Valid LinkUpdate update) {
        List<SendMessage> requests = UpdateParser.toRequestList(update);

        for (SendMessage request : requests) {
            bot.execute(request);
        }
    }
}
