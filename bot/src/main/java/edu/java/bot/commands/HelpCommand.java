package edu.java.bot.commands;

import com.pengrad.telegrambot.model.BotCommand;
import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.SendMessage;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class HelpCommand implements Command {

    private static final String COMMAND = "/help";
    private static final String DESCRIPTION = "Вывести список команд";
    private List<BotCommand> commandList;

    @Autowired
    public HelpCommand(List<? extends Command> commands) {
        this.commandList = commands.stream()
            .map(Command::botCommand)
            .toList();
    }

    public void setCommands(List<BotCommand> commandList) {
        this.commandList = commandList;
    }

    @Override
    public SendMessage execute(Update update) {
        Chat chat = update.message().chat();

        StringBuilder message = new StringBuilder("*Список команд*\n\n");

        for (BotCommand command : commandList) {
            message.append(command.command()).append(" - ").append(command.description()).append('\n');
        }

        return new SendMessage(chat.id(), message.toString())
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
