package edu.java.bot.service;

import com.pengrad.telegrambot.model.BotCommand;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.service.command.Command;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Metrics;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class CommandPool {

    private static final String NO_SUPPORT_MESSAGE = "Команда не поддерживается или написана неправильно!";

    private final List<Command> commands;
    private final Counter counter;

    public CommandPool(List<Command> commands) {
        this.commands = commands;
        this.counter = Metrics.counter("proceed.messages");
    }

    public List<BotCommand> getBotCommands() {
        return commands.stream()
            .map(Command::botCommand)
            .toList();
    }

    public SendMessage process(Update update) {
        if (update == null || update.message() == null) {
            return null;
        }
        counter.increment();

        for (Command command : commands) {
            if (command.supports(update)) {
                return command.execute(update);
            }
        }

        return new SendMessage(update.message().chat().id(), NO_SUPPORT_MESSAGE);
    }
}
