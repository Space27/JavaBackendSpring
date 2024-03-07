package edu.java.bot.controller.updatesApi;

import edu.java.bot.controller.request.LinkUpdateRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/updates")
@RequiredArgsConstructor
public class UpdatesApiController implements UpdatesApi {

    private final UpdateHandler updateHandler;

    @Override
    public ResponseEntity<Void> update(LinkUpdateRequest update) {
        updateHandler.handleUpdate(update);

        return ResponseEntity.ok().build();
    }
}
