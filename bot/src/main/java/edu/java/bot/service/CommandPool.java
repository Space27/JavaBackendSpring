package edu.java.bot.service;

import com.pengrad.telegrambot.model.BotCommand;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.repository.LinkRepository;
import edu.java.bot.service.command.Command;
import edu.java.bot.service.command.HelpCommand;
import edu.java.bot.service.command.ListCommand;
import edu.java.bot.service.command.StartCommand;
import edu.java.bot.service.command.TrackCommand;
import edu.java.bot.service.command.UntrackCommand;
import java.util.ArrayList;
import java.util.List;

public class CommandPool {

    private static final String NO_SUPPORT_MESSAGE = "Команда не поддерживается или написана неправильно!";

    private final List<Command> commands;

    public CommandPool(List<Command> commands) {
        this.commands = commands;
    }

    public static CommandPool standardPool(LinkRepository linkRepository) {
        List<Command> tmpCommands = new ArrayList<>(List.of(
            new StartCommand(linkRepository),
            new TrackCommand(linkRepository),
            new UntrackCommand(linkRepository),
            new ListCommand(linkRepository),
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
