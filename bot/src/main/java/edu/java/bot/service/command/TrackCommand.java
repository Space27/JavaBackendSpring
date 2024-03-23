package edu.java.bot.service.command;

import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.service.LinkService;
import edu.java.bot.util.MessageParserUtil;
import java.net.URI;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TrackCommand implements Command {

    private static final String COMMAND = "/track";
    private static final String DESCRIPTION = "Начать отслеживание ссылки";
    private static final String NOT_STARTED = "Вы не зарегистрированы в системе!";
    private static final String NOT_VALID_LINK = "Ссылка введена в неправильном формате!";
    private static final String COMMON_MESSAGE = "Начато отслеживание ссылки %s";
    private static final String LINK_ALREADY_TRACKING = "Вы уже отслеживаете эту ссылку!";
    private static final String LINK_NOT_SUPPORTED = "Данная ссылка не поддерживается";
    private static final String UNEXPECTED_ERROR = "Произошла непредвиденная ошибка";

    private final LinkService linkService;

    @Override
    public SendMessage execute(Update update) {
        Chat chat = update.message().chat();

        String result;
        Optional<URI> uri = MessageParserUtil.getURI(update.message());

        if (uri.isPresent()) {
            result = switch (linkService.addLink(chat.id(), uri.get())) {
                case OK -> String.format(COMMON_MESSAGE, uri.get());
                case CHAT_NOT_EXISTS -> NOT_STARTED;
                case LINK_ALREADY_TRACKING -> LINK_ALREADY_TRACKING;
                case NOT_VALID_REQUEST -> LINK_NOT_SUPPORTED;
                default -> UNEXPECTED_ERROR;
            };
        } else {
            result = NOT_VALID_LINK;
        }

        return new SendMessage(chat.id(), result)
            .disableWebPagePreview(true);
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
