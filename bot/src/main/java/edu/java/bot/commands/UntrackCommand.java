package edu.java.bot.commands;

import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.LinkStorage;
import edu.java.bot.MessageParser;
import java.net.URI;

public class UntrackCommand implements Command {

    private static final String COMMAND = "/untrack";
    private static final String DESCRIPTION = "Прекратить отслеживание ссылки";
    private static final String NOT_STARTED = "Вы не зарегистрированы в системе!";
    private final LinkStorage storage;

    public UntrackCommand(LinkStorage linkStorage) {
        this.storage = linkStorage;
    }

    @Override
    public SendMessage execute(Update update) {
        Chat chat = update.message().chat();
        if (!storage.contains(chat.id())) {
            return new SendMessage(chat.id(), NOT_STARTED);
        }

        String result;
        URI uri = MessageParser.getURI(update.message());

        if (uri != null) {
            if (storage.get(chat.id()).contains(uri.toString())) {
                storage.remove(chat.id(), uri.toString());
                result = String.format("Прекращено отслеживание ссылки %s", uri);
            } else {
                result = "Данная ссылка не отслеживается!";
            }
        } else {
            result = "Ссылка введена в неправильном формате!";
        }

        return new SendMessage(chat.id(), result)
            .disableWebPagePreview(true);
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
