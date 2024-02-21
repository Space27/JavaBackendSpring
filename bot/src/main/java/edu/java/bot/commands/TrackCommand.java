package edu.java.bot.commands;

import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.LinkStorage;
import edu.java.bot.MessageParser;
import java.net.URI;
import java.util.Optional;

public class TrackCommand implements Command {

    private static final String COMMAND = "/track";
    private static final String DESCRIPTION = "Начать отслеживание ссылки";
    private static final String NOT_STARTED = "Вы не зарегистрированы в системе!";
    private final LinkStorage storage;

    public TrackCommand(LinkStorage linkStorage) {
        this.storage = linkStorage;
    }

    @Override
    public SendMessage execute(Update update) {
        Chat chat = update.message().chat();
        if (!storage.contains(chat.id())) {
            return new SendMessage(chat.id(), NOT_STARTED);
        }

        String result;
        Optional<URI> uri = MessageParser.getURI(update.message());

        if (uri.isPresent()) {
            storage.add(chat.id(), uri.get().toString());
            result = String.format("Начато отслеживание ссылки %s", uri.get());
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
