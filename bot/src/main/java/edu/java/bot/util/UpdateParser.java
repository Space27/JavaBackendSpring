package edu.java.bot.util;

import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.service.api.schemas.LinkUpdate;
import java.util.ArrayList;
import java.util.List;
import lombok.experimental.UtilityClass;

@UtilityClass
public class UpdateParser {

    public List<SendMessage> toRequestList(LinkUpdate linkUpdate) {
        List<SendMessage> requests = new ArrayList<>();

        for (Long chatID : linkUpdate.tgChatIds()) {
            String message = String.format(
                "*Пришло обновление!*\n\nСсылка: %s\nОбновление: %s",
                linkUpdate.url(),
                linkUpdate.description()
            );

            requests.add(new SendMessage(chatID, message)
                .parseMode(ParseMode.Markdown)
                .disableWebPagePreview(true));
        }

        return requests;
    }
}
