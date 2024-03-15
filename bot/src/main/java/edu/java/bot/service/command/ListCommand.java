package edu.java.bot.service.command;

import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.service.LinkService;
import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ListCommand implements Command {

    private static final String COMMAND = "/list";
    private static final String DESCRIPTION = "Вывести список отслеживаемых ссылок";
    private static final String NOT_STARTED = "Вы не зарегистрированы в системе!";
    private static final String EMPTY_LIST = "У Вас нет отслеживаемых ссылок!";

    private final LinkService linkService;

    @Override
    public SendMessage execute(Update update) {
        Chat chat = update.message().chat();

        String result;
        List<URI> links = linkService.getLinks(chat.id());
        if (links == null) {
            result = NOT_STARTED;
        } else if (links.isEmpty()) {
            result = EMPTY_LIST;
        } else {
            result = "*Список ссылок*\n\n" + links.stream()
                .map(link -> "- " + link.toString())
                .collect(Collectors.joining("\n"));
        }

        return new SendMessage(chat.id(), result)
            .disableWebPagePreview(true)
            .parseMode(ParseMode.Markdown);
    }

    @Override
    public String command() {
        return COMMAND;
    }

    @Override
    public String description() {
        return DESCRIPTION;
    }
}
