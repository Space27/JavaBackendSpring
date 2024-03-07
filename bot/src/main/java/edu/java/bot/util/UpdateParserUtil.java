package edu.java.bot.util;

import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.controller.request.LinkUpdateRequest;
import java.util.ArrayList;
import java.util.List;
import lombok.experimental.UtilityClass;

@UtilityClass
public class UpdateParserUtil {

    public List<SendMessage> toRequestList(LinkUpdateRequest linkUpdateRequest) {
        List<SendMessage> requests = new ArrayList<>();

        for (Long chatID : linkUpdateRequest.tgChatIds()) {
            String message = String.format(
                "*Пришло обновление!*\n\nСсылка: %s\nОбновление: %s",
                linkUpdateRequest.url(),
                linkUpdateRequest.description()
            );

            requests.add(new SendMessage(chatID, message)
                .parseMode(ParseMode.Markdown)
                .disableWebPagePreview(true));
        }

        return requests;
    }
}
