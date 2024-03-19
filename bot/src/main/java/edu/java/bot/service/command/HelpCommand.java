package edu.java.bot.service.command;

import com.pengrad.telegrambot.model.BotCommand;
import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.SendMessage;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.springframework.stereotype.Component;

@Component
public class HelpCommand implements Command {

    private static final String COMMAND = "/help";
    private static final String DESCRIPTION = "Вывести список команд";
    private final List<BotCommand> commandList;

    public HelpCommand(List<Command> commands) {
        this.commandList = Stream.concat(commands.stream(), Stream.of(this))
            .distinct()
            .map(Command::botCommand)
            .toList();
    }

    @Override
    public SendMessage execute(Update update) {
        Chat chat = update.message().chat();

        String message = "*Список команд*\n\n" + commandList.stream()
            .map(command -> command.command() + " - " + command.description())
            .collect(Collectors.joining("\n"));

        return new SendMessage(chat.id(), message)
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
