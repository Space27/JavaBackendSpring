package edu.java.bot.commands;

import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.LinkStorage;
import edu.java.bot.MessageParser;
import java.net.URI;

public class TrackCommand implements Command {

    private static final String COMMAND = "/track";
    private static final String DESCRIPTION = "Начать отслеживание ссылки";
    private final LinkStorage storage;

    public TrackCommand(LinkStorage linkStorage) {
        this.storage = linkStorage;
    }

    @Override
    public SendMessage execute(Update update) {
        String result;
        URI uri = MessageParser.getURI(update.message());

        if (uri != null) {
            storage.add(update.message().chat().id(), uri.toString());
            result = String.format("Начато отслеживание ссылки %s", uri);
        } else {
            result = "Ссылка введена в неправильном формате!";
        }

        return new SendMessage(update.message().chat().id(), result)
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
