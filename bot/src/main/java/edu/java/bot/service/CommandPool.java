package edu.java.bot.service;

import com.pengrad.telegrambot.model.BotCommand;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.service.commands.Command;
import edu.java.bot.service.commands.HelpCommand;
import edu.java.bot.service.commands.ListCommand;
import edu.java.bot.service.commands.StartCommand;
import edu.java.bot.service.commands.TrackCommand;
import edu.java.bot.service.commands.UntrackCommand;
import edu.java.bot.repository.LinkStorage;

import java.util.ArrayList;
import java.util.List;

public class CommandPool {

    private static final String NO_SUPPORT_MESSAGE = "Команда не поддерживается или написана неправильно!";

    private final List<Command> commands;

    public CommandPool(List<Command> commands) {
        this.commands = commands;
    }

    public static CommandPool standardPool(LinkStorage linkStorage) {
        List<Command> tmpCommands = new ArrayList<>(List.of(
            new StartCommand(linkStorage),
            new TrackCommand(linkStorage),
            new UntrackCommand(linkStorage),
            new ListCommand(linkStorage),
            new HelpCommand(List.of())
        ));
        ((HelpCommand) tmpCommands.getLast()).setCommands(tmpCommands);

        return new CommandPool(tmpCommands);
    }

    public List<BotCommand> getBotCommands() {
        return commands.stream()
            .map(Command::botCommand)
            .toList();
    }

    public SendMessage process(Update update) {
        for (Command command : commands) {
            if (command.supports(update)) {
                return command.execute(update);
            }
        }

        return update.message() != null ? new SendMessage(update.message().chat().id(), NO_SUPPORT_MESSAGE) : null;
    }
}
