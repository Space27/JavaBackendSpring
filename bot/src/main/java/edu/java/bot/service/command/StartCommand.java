package edu.java.bot.service.command;

import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StartCommand implements Command {

    private static final String COMMAND = "/start";
    private static final String DESCRIPTION = "Начать работу с ботом";
    private static final String ALREADY_REGISTERED = "Вы уже зарегистрированы в системе!";
    private static final String COMMON_MESSAGE = "Поздравляю, %s, Вы можете начинать отслеживание ссылок!";

    private final ChatService chatService;

    @Override
    public SendMessage execute(Update update) {
        Chat chat = update.message().chat();
        String result;

        if (chatService.register(chat.id())) {
            result = String.format(COMMON_MESSAGE, chat.firstName());
        } else {
            result = ALREADY_REGISTERED;
        }

        return new SendMessage(chat.id(), result);
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
