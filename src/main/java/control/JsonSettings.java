package control;

import com.google.gson.Gson;
import interfaces.IConfiguration;
import models.Settings;

import java.io.InputStream;

public class JsonSettings implements IConfiguration {

    final String IP;
    final int port;
    final String username;
    final String password;

    public JsonSettings() {
        InputStream settingsStream = getClass().getResourceAsStream("control/settings.json");
        String jsonData;

        try(java.util.Scanner s = new java.util.Scanner(settingsStream)) {
            jsonData = s.useDelimiter("\\A").hasNext() ? s.next() : "";
        }

        Gson gson = new Gson();
        Settings settings = gson.fromJson(jsonData, Settings.class);

        IP = settings.getHubConnectDetails().getAddress();
        port = settings.getHubConnectDetails().getPort().intValue();
        username = settings.getUserDetails().getUsername();
        password = settings.getUserDetails().getPassword();
    }

    @Override
    public void setUsername(String username) {

    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public void setPassword(String password) {

    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getIPAddress() {
        return IP;
    }

    @Override
    public int getPort() {
        return port;
    }
}
