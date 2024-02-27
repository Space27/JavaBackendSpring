package edu.java.bot.service.commands;

import com.pengrad.telegrambot.model.BotCommand;
import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.SendMessage;
import java.util.List;

public class HelpCommand implements Command {

    private static final String COMMAND = "/help";
    private static final String DESCRIPTION = "Вывести список команд";
    private List<BotCommand> commandList;

    public HelpCommand(List<Command> commands) {
        this.commandList = commands.stream()
            .map(Command::botCommand)
            .toList();
    }

    public void setCommands(List<Command> commands) {
        this.commandList = commands.stream()
            .map(Command::botCommand)
            .toList();
    }

    public void addCommand(Command command) {
        if (command == null) {
            return;
        }

        addCommand(command.botCommand());
    }

    public void addCommand(BotCommand command) {
        if (command == null) {
            return;
        }

        commandList.add(command);
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
