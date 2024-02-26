package edu.java.bot.util;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import lombok.experimental.UtilityClass;

@UtilityClass
public class Link {

    public boolean checkURI(String strURI) {
        if (strURI == null) {
            return false;
        }

        try {
            URI uri = new URI(strURI);

            return checkURI(uri);
        } catch (URISyntaxException e) {
            return false;
        }
    }

    public boolean checkURI(URI uri) {
        if (uri == null) {
            return false;
        }

        HttpURLConnection huc = null;

        try {
            huc = (HttpURLConnection) uri.toURL().openConnection();
            int responseCode = huc.getResponseCode();

            return responseCode == HttpURLConnection.HTTP_OK;
        } catch (IllegalArgumentException | IOException e) {
            return false;
        } finally {
            if (huc != null) {
                huc.disconnect();
            }
        }
    }
}
