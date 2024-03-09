package edu.java.bot.controller.updatesApi;

import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.controller.request.LinkUpdateRequest;
import edu.java.bot.telegram.IBot;
import edu.java.bot.util.UpdateParserUtil;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UpdateHandler {

    private final IBot bot;

    public void handleUpdate(LinkUpdateRequest update) {
        List<SendMessage> requests = UpdateParserUtil.toRequestList(update);

        for (SendMessage request : requests) {
            bot.execute(request);
        }
    }
}
