package edu.java.bot.commands;

import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.storage.LinkStorage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class StartCommand implements Command {

    private static final String COMMAND = "/start";
    private static final String DESCRIPTION = "Начать работу с ботом";
    private final LinkStorage storage;

    @Autowired
    public StartCommand(LinkStorage linkStorage) {
        this.storage = linkStorage;
    }

    @Override
    public SendMessage execute(Update update) {
        Chat chat = update.message().chat();
        String result;

        if (!storage.contains(chat.id())) {
            result = String.format("Поздравляю, %s, Вы можете начинать отслеживание ссылок!", chat.firstName());
            storage.addChat(chat.id());
        } else {
            result = "Вы уже зарегистрированы в системе!";
        }

        return new SendMessage(chat.id(), result);
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
