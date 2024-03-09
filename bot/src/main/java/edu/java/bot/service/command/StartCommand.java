package edu.java.bot.service.command;

import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.repository.LinkRepository;

public class StartCommand implements Command {

    private static final String COMMAND = "/start";
    private static final String DESCRIPTION = "Начать работу с ботом";
    private final LinkRepository storage;


    public StartCommand(LinkRepository linkRepository) {
        this.storage = linkRepository;
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
