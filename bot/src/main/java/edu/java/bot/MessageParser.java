package edu.java.bot;

import com.pengrad.telegrambot.model.Message;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;

public final class MessageParser {

    private MessageParser() {
    }

    public static String getCommand(Message message) {
        if (message == null || message.text() == null) {
            return null;
        }

        String string = message.text();
        String command = string.split(" ", 2)[0];

        if (command != null && !command.isEmpty() && command.length() > 1 && command.charAt(0) == '/') {
            return command;
        } else {
            return null;
        }
    }

    public static URI getURI(Message message) {
        if (message == null || message.text() == null) {
            return null;
        }

        String string = message.text();
        String[] splitString = string.split(" ");

        if (splitString.length >= 1) {
            for (String strURI : splitString) {
                if (checkURI(strURI)) {
                    try {
                        return new URI(strURI);
                    } catch (URISyntaxException ignored) {
                    }
                }
            }
            return null;
        }
        return null;
    }

    private static boolean checkURI(String strURI) {
        try {
            URI uri = new URI(strURI);

            HttpURLConnection huc = (HttpURLConnection) uri.toURL().openConnection();
            int responseCode = huc.getResponseCode();
            huc.disconnect();

            return responseCode == HttpURLConnection.HTTP_OK;
        } catch (URISyntaxException | IllegalArgumentException | IOException e) {
            return false;
        }
    }
}
