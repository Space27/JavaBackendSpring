package edu.java.bot;

import com.pengrad.telegrambot.model.BotCommand;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.commands.Command;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CommandPool {

    private static final String NO_SUPPORT_MESSAGE = "Команда не поддерживается или написана неправильно!";

    private final List<? extends Command> commands;

    @Autowired
    public CommandPool(List<? extends Command> commands) {
        this.commands = commands;
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
