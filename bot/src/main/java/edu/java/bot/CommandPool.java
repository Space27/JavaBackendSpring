package edu.java.bot;

import com.pengrad.telegrambot.model.BotCommand;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.commands.Command;
import edu.java.bot.commands.HelpCommand;
import edu.java.bot.commands.ListCommand;
import edu.java.bot.commands.StartCommand;
import edu.java.bot.commands.TrackCommand;
import edu.java.bot.commands.UntrackCommand;
import java.util.ArrayList;
import java.util.List;

public class CommandPool {

    private static final String NO_SUPPORT_MESSAGE = "Команда не поддерживается или написана неправильно!";

    private final List<? extends Command> commands;

    public CommandPool(List<? extends Command> commands) {
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
        HelpCommand helpCommand = (HelpCommand) tmpCommands.getLast();
        helpCommand.setCommands(tmpCommands.stream()
            .map(Command::botCommand)
            .toList());

        return new CommandPool(tmpCommands);
    }

    public List<? extends Command> getCommands() {
        return commands;
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
