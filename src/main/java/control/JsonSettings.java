package control;

import com.google.gson.Gson;
import interfaces.IConfiguration;
import models.settings.Settings;

import java.io.FileInputStream;

public class JsonSettings implements IConfiguration {
    private final boolean DEBUG = true;

    final String IP;
    final int port;
    final String username;
    final String password;

    public JsonSettings() {
        String jsonData = "";
        try {
            //TODO Find a better way to store the settings file
            FileInputStream settingsStream = new FileInputStream("/tmp/settings.json");
            try (java.util.Scanner s = new java.util.Scanner(settingsStream)) {
                jsonData = s.useDelimiter("\\A").hasNext() ? s.next() : "";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        Gson gson = new Gson();
        Settings settings = gson.fromJson(jsonData, Settings.class);

        String tmpIP = settings.getHubConnectDetails().getAddress();
        if (tmpIP.startsWith("dchub://")) {
            IP = tmpIP.replace("dchub://", "");
        } else{
            IP = tmpIP;
        }
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

    @Override
    public boolean isDebugOn() {
        return DEBUG;
    }
}
