package edu.java.bot.controller.updatesApi;

import edu.java.bot.controller.request.LinkUpdateRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/updates")
@RequiredArgsConstructor
public class UpdatesApiController implements UpdatesApi {

    private final UpdateHandler updateHandler;

    @Override
    public void update(LinkUpdateRequest update) {
        updateHandler.handleUpdate(update);
    }
}
