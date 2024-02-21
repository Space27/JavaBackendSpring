package edu.java.bot.service.commands;

import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.repository.LinkStorage;
import java.util.List;

public class ListCommand implements Command {

    private static final String COMMAND = "/list";
    private static final String DESCRIPTION = "Вывести список отслеживаемых ссылок";
    private static final String NOT_STARTED = "Вы не зарегистрированы в системе!";
    private final LinkStorage storage;

    public ListCommand(LinkStorage linkStorage) {
        this.storage = linkStorage;
    }

    @Override
    public SendMessage execute(Update update) {
        Chat chat = update.message().chat();
        if (!storage.contains(chat.id())) {
            return new SendMessage(chat.id(), NOT_STARTED);
        }

        List<String> links = storage.get(chat.id());
        String result;

        if (links != null && !links.isEmpty()) {
            StringBuilder message = new StringBuilder("*Список ссылок*\n\n");

            for (String link : links) {
                message.append("- ").append(link).append('\n');
            }

            result = message.toString();
        } else {
            result = "У Вас нет отслеживаемых ссылок!";
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
