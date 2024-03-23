package edu.java.bot.service.command;

import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EndCommand implements Command {

    private static final String COMMAND = "/end";
    private static final String DESCRIPTION = "Завершить работу с ботом";
    private static final String NOT_REGISTERED = "Вы не зарегистрированы в системе!";
    private static final String COMMON_MESSAGE = "Работа с ботом завершена. Ждём вас снова!";

    private final ChatService chatService;

    @Override
    public SendMessage execute(Update update) {
        Chat chat = update.message().chat();
        String result;

        if (chatService.unregister(chat.id())) {
            result = COMMON_MESSAGE;
        } else {
            result = NOT_REGISTERED;
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
