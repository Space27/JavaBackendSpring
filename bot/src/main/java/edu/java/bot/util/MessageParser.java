package edu.java.bot.util;

import com.pengrad.telegrambot.model.Message;
import lombok.experimental.UtilityClass;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;

@UtilityClass
public final class MessageParser {

    public static Optional<String> getCommand(Message message) {
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

    public static Optional<URI> getURI(Message message) {
        if (message == null || message.text() == null) {
            return Optional.empty();
        }

        String string = message.text();
        String[] splitString = string.split(" ");

        if (splitString.length >= 1) {
            for (String strURI : splitString) {
                if (checkURI(strURI)) {
                    try {
                        return Optional.of(new URI(strURI));
                    } catch (URISyntaxException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
            return Optional.empty();
        }
        return Optional.empty();
    }

    private static boolean checkURI(String strURI) {
        HttpURLConnection huc = null;

        try {
            URI uri = new URI(strURI);

            huc = (HttpURLConnection) uri.toURL().openConnection();
            int responseCode = huc.getResponseCode();

            return responseCode == HttpURLConnection.HTTP_OK;
        } catch (URISyntaxException | IllegalArgumentException | IOException e) {
            return false;
        } finally {
            if (huc != null) {
                huc.disconnect();
            }
        }
    }
}
