package edu.java.bot.util;

import com.pengrad.telegrambot.model.Message;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;
import lombok.experimental.UtilityClass;

@UtilityClass
public final class MessageParserUtil {

    public Optional<String> getCommand(Message message) {
        if (message == null || message.text() == null) {
            return Optional.empty();
        }

        String string = message.text();
        String command = string.split(" ", 2)[0];

        if (command != null && !command.isEmpty() && command.length() > 1 && command.charAt(0) == '/') {
            return Optional.of(command);
        } else {
            return Optional.empty();
        }
    }

    public Optional<URI> getURI(Message message) {
        if (message == null || message.text() == null) {
            return Optional.empty();
        }

        String string = message.text();
        String[] splitString = string.split(" ");

        if (splitString.length >= 1) {
            for (String strURI : splitString) {
                if (LinksUtil.checkURI(strURI)) {
                    try {
                        return Optional.of(new URI(strURI));
                    } catch (URISyntaxException ignored) {
                    }
                }
            }
            return Optional.empty();
        }
        return Optional.empty();
    }
}
