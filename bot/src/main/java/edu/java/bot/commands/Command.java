package edu.java.bot.commands;

import com.pengrad.telegrambot.model.BotCommand;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.MessageParser;
import java.util.Optional;

public interface Command {

    SendMessage execute(Update update);

    String command();

    String description();

    default BotCommand botCommand() {
        return new BotCommand(command(), description());
    }

    default boolean supports(Update update) {
        Optional<String> command = MessageParser.getCommand(update.message());

        return command.isPresent() && command.get().equals(command());
    }
}
