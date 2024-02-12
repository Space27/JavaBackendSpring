package edu.java.bot.commands;

import com.pengrad.telegrambot.model.BotCommand;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.SendMessage;
import java.util.List;

public class HelpCommand implements Command {

    private static final String COMMAND = "/help";
    private static final String DESCRIPTION = "Вывести список команд";
    private List<BotCommand> commandList;

    public HelpCommand(List<BotCommand> commandList) {
        this.commandList = commandList;
    }

    public void setCommands(List<BotCommand> commandList) {
        this.commandList = commandList;
    }

    @Override
    public SendMessage execute(Update update) {
        StringBuilder message = new StringBuilder("*Список команд*\n\n");

        for (BotCommand command : commandList) {
            message.append(command.command()).append(" - ").append(command.description()).append('\n');
        }

        return new SendMessage(update.message().chat().id(), message.toString())
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
