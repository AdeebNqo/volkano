package models;

import interfaces.IConfiguration;

import java.util.List;

// setting used when testing
public class TestSettingsConfiguration implements IConfiguration {

    private final boolean DEBUG = true;
    private static final String configurationFile = "";

    private String username = "newUser";
    private String password = "somepassword";
    private int port = 9090;
    private String IPAddress = "localhost";

    public TestSettingsConfiguration() {}

    public void setUsername(String username){
            this.username = username;
    }
    public String getUsername(){
            return username;
    }
    public void setPassword(String password){
            this.password = password;
    }
    public String getPassword(){
            return password;
    }
    public String getIPAddress(){
            return IPAddress;
    }
    public int getPort(){
            return port;
    }

    @Override
    public boolean isDebugOn() {
        return DEBUG;
    }

    @Override
    public List<String> getSupportedFeatures() {
        return null;
    }

    @Override
    public String getDescription() {
        return "i am god";
    }

    @Override
    public String getEmail() {
        return "test@mctestface.com";
    }
}
