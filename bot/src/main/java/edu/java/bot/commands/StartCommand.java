package edu.java.bot.commands;

import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.LinkStorage;

public class StartCommand implements Command {

    private static final String COMMAND = "/start";
    private static final String DESCRIPTION = "Начать работу с ботом";
    private final LinkStorage storage;

    public StartCommand(LinkStorage linkStorage) {
        this.storage = linkStorage;
    }

    @Override
    public SendMessage execute(Update update) {
        String result;
        if (storage.get(update.message().chat().id()) == null) {
            result = String.format(
                "Поздравляю, %s, Вы можете начинать отслеживание ссылок!",
                update.message().chat().firstName()
            );
            storage.addChat(update.message().chat().id());
        } else {
            result = "Вы уже зарегистрированы в системе!";
        }
        return new SendMessage(update.message().chat().id(), result);
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
