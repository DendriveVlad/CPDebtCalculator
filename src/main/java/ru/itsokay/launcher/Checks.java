package ru.itsokay.launcher;

import javafx.scene.web.WebEngine;
import netscape.javascript.JSObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class Checks {
    private final JavaConnector javaConnector;
    private JSObject javascriptConnector;

    public Checks(JSObject jsConnector, JavaConnector connector) {
        javaConnector = connector;
        javascriptConnector = jsConnector;
    }

    public boolean checkInternetConnection() {
        try {
            final URL url = new URL("https://www.google.com");
            final URLConnection conn = url.openConnection();
            conn.connect();
            conn.getInputStream().close();
            return true;
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            return false;
        }
    }
}
